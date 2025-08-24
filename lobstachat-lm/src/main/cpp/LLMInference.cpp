#include "LLMInference.h"
#include "llama.h"
#include "gguf.h"
#include "common.h"
#include <android/log.h>
#include <cstring>
#include <iostream>
#include <vector>

#define TAG "[SmolLMAndroid-Cpp]"
#define LOGi(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGe(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

void
LLMInference::loadModel(const char* model_path, const InferenceParams& params) {
    LOGi("loading model with"
         "\n\tmodel_path = %s"
         "\n\tminP = %f"
         "\n\ttemperature = %f"
         "\n\tstoreChats = %d"
         "\n\tcontextSize = %d"
         "\n\tchatTemplate = %s"
         "\n\tnThreads = %d"
         "\n\tuseMmap = %d"
         "\n\tuseMlock = %d"
         "\n\ttopP = %f"
         "\n\ttopK = %i"
         "\n\txtcP = %f"
         "\n\txtcT = %f",
         model_path, params.minP, params.temperature, (int)params.storeChats, (int)params.contextSize, params.chatTemplate, params.nThreads, params.useMmap, params.useMlock, params.topP, params.topK, params.xtcP, params.xtcT);

    llama_backend_init();
    // create an instance of llama_model
    llama_model_params model_params = llama_model_default_params();
    model_params.use_mmap           = params.useMmap;
    model_params.use_mlock          = params.useMlock;
    _model                          = llama_model_load_from_file(model_path, model_params);

    if (!_model) {
        LOGe("failed to load model from %s", model_path);
        throw std::runtime_error("loadModel() failed");
    }

    // create an instance of llama_context
    llama_context_params ctx_params = llama_context_default_params();
    ctx_params.n_ctx                = params.contextSize;
    ctx_params.n_threads            = params.nThreads;
    _ctx                            = llama_init_from_model(_model, ctx_params);

    if (!_ctx) {
        LOGe("llama_init_from_model() returned null)");
        throw std::runtime_error("llama_init_from_model() returned null");
    }

    // initialize sampler
    _sampler = llama_sampler_init_temp(nullptr, params.temperature);

    _formattedMessages = std::vector<char>(llama_n_ctx(_ctx));
    _messages.clear();
    _chatTemplate     = strdup(params.chatTemplate);
    this->_storeChats = params.storeChats;
    _batch = llama_batch_init(params.contextSize, 0, 1);
}

void
LLMInference::addChatMessage(const char* message, const char* role) {
    _messages.push_back({ strdup(role), strdup(message) });
}

float
LLMInference::getResponseGenerationTime() const {
    return (float)_responseNumTokens / (_responseGenerationTime / 1e6);
}

int
LLMInference::getContextSizeUsed() const {
    return _nCtxUsed;
}

void
LLMInference::startCompletion(const char* query) {
    if (!_storeChats) {
        _prevLen = 0;
        _formattedMessages.clear();
        _formattedMessages = std::vector<char>(llama_n_ctx(_ctx));
    }
    _responseGenerationTime = 0;
    _responseNumTokens      = 0;
    addChatMessage(query, "user");
    // apply the chat-template
    int newLen = llama_chat_apply_template(nullptr, _chatTemplate, _messages.data(), _messages.size(), true,
                                           _formattedMessages.data(), _formattedMessages.size());
    if (newLen > (int)_formattedMessages.size()) {
        // resize the output buffer `_formattedMessages`
        // and re-apply the chat template
        _formattedMessages.resize(newLen);
        newLen = llama_chat_apply_template(nullptr, _chatTemplate, _messages.data(), _messages.size(), true,
                                           _formattedMessages.data(), _formattedMessages.size());
    }
    if (newLen < 0) {
        throw std::runtime_error("llama_chat_apply_template() in LLMInference::startCompletion() failed");
    }
    std::string prompt(_formattedMessages.begin() + _prevLen, _formattedMessages.begin() + newLen);

    std::vector<llama_token> tokens_list;
    tokens_list = ::llama_tokenize(_model, prompt, true);

    _batch.n_tokens = tokens_list.size();
    for (size_t i = 0; i < tokens_list.size(); ++i) {
        _batch.token[i]  = tokens_list[i];
        _batch.pos[i]    = _nCtxUsed + i;
        _batch.seq_id[i] = 0;
    }
    _batch.logits[tokens_list.size() - 1] = 1;
}

// taken from:
// https://github.com/ggerganov/llama.cpp/blob/master/examples/llama.android/llama/src/main/cpp/llama-android.cpp#L38
bool
LLMInference::_isValidUtf8(const char* response) {
    if (!response) {
        return true;
    }
    const unsigned char* bytes = (const unsigned char*)response;
    int                  num;
    while (*bytes != 0x00) {
        if ((*bytes & 0x80) == 0x00) {
            // U+0000 to U+007F
            num = 1;
        } else if ((*bytes & 0xE0) == 0xC0) {
            // U+0080 to U+07FF
            num = 2;
        } else if ((*bytes & 0xF0) == 0xE0) {
            // U+0800 to U+FFFF
            num = 3;
        } else if ((*bytes & 0xF8) == 0xF0) {
            // U+10000 to U+10FFFF
            num = 4;
        } else {
            return false;
        }

        bytes += 1;
        for (int i = 1; i < num; ++i) {
            if ((*bytes & 0xC0) != 0x80) {
                return false;
            }
            bytes += 1;
        }
    }
    return true;
}

std::string
LLMInference::completionLoop() {
    // check if the length of the inputs to the model
    // have exceeded the context size of the model
    uint32_t contextSize = llama_n_ctx(_ctx);
    _nCtxUsed            = llama_get_kv_cache_token_count(_ctx);
    if (_nCtxUsed + _batch.n_tokens > contextSize) {
        throw std::runtime_error("context size reached");
    }

    auto start = ggml_time_us();
    // run the model
    if (llama_decode(_ctx, _batch) < 0) {
        throw std::runtime_error("llama_decode() failed");
    }

    auto * logits  = llama_get_logits_ith(_ctx, _batch.n_tokens - 1);
    auto n_vocab = llama_n_vocab(llama_get_model(_ctx));

    std::vector<llama_token_data> candidates;
    candidates.reserve(n_vocab);
    for (llama_token token_id = 0; token_id < n_vocab; token_id++) {
        candidates.push_back({token_id, logits[token_id], 0.0f});
    }

    llama_token_data_array candidates_p = { candidates.data(), candidates.size(), false };

    // sample a token and check if it is an EOG (end of generation token)
    // convert the integer token to its corresponding word-piece
    _currToken = llama_sample_token_greedy(_ctx, &candidates_p);
    llama_sampler_accept(_sampler, _ctx, _currToken);

    if (llama_token_is_eog(llama_get_model(_ctx), _currToken)) {
        addChatMessage(strdup(_response.data()), "assistant");
        _response.clear();
        return "";
    }
    std::string piece = llama_token_to_piece(_ctx, _currToken);
    LOGi("common_token_to_piece: %s", piece.c_str());
    auto end = ggml_time_us();
    _responseGenerationTime += (end - start);
    _responseNumTokens += 1;
    _cacheResponseTokens += piece;

    // re-init the batch with the newly predicted token
    // key, value pairs of all previous tokens have been cached
    // in the KV cache
    _batch.n_tokens = 1;
    _batch.token[0] = _currToken;
    _batch.pos[0] = _nCtxUsed;
    _batch.seq_id[0] = 0;

    if (_isValidUtf8(_cacheResponseTokens.c_str())) {
        _response += _cacheResponseTokens;
        std::string valid_utf8_piece = _cacheResponseTokens;
        _cacheResponseTokens.clear();
        return valid_utf8_piece;
    }

    return "";
}

void
LLMInference::stopCompletion() {
    if (_storeChats) {
        addChatMessage(_response.c_str(), "assistant");
    }
    _response.clear();
    const char* tmpl = llama_model_chat_template(_model, nullptr);
    _prevLen         = llama_chat_apply_template(nullptr, tmpl, _messages.data(), _messages.size(), false, nullptr, 0);
    if (_prevLen < 0) {
        throw std::runtime_error("llama_chat_apply_template() in LLMInference::stopCompletion() failed");
    }
}

void
LLMInference::saveSession(const char* filePath) {
    if (!_ctx) {
        return;
    }
    llama_state_save_file(_ctx, filePath, nullptr, 0);
}

void
LLMInference::loadSession(const char* filePath) {
    if (!_ctx) {
        return;
    }
    size_t n_token_count_out = 0;
    llama_state_load_file(_ctx, filePath, nullptr, 0, &n_token_count_out);
}

void
LLMInference::clearContext() {
    if (!_ctx) {
        return;
    }
    llama_memory_clear(llama_get_memory(_ctx), true);
}

LLMInference::~LLMInference() {
    LOGi("deallocating LLMInference instance");
    // free memory held by the message text in messages
    // (as we had used strdup() to create a malloc'ed copy)
    for (llama_chat_message& message : _messages) {
        free(const_cast<char*>(message.role));
        free(const_cast<char*>(message.content));
    }
    free(const_cast<char*>(_chatTemplate));
    llama_batch_free(_batch);
    llama_free_samplers(_sampler);
    llama_free(_ctx);
    llama_free_model(_model);
    llama_backend_free();
}