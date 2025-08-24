#include "llama.h"
#include <jni.h>
#include <string>
#include <vector>

struct InferenceParams {
    float minP;
    float temperature;
    bool storeChats;
    long contextSize;
    const char* chatTemplate;
    int nThreads;
    bool useMmap;
    bool useMlock;
    float topP;
    int topK;
    float xtcP;
    float xtcT;
};

class LLMInference {
private:
    bool _isValidUtf8(const char* response);

    struct llama_model*  _model   = nullptr;
    struct llama_context* _ctx     = nullptr;
    struct llama_batch   _batch;
    struct llama_sampler* _sampler = nullptr;
    std::vector<llama_chat_message> _messages;
    const char*          _chatTemplate;
    std::vector<char>    _formattedMessages;
    std::string          _response;
    std::string          _cacheResponseTokens;
    bool                 _storeChats;
    int                  _nCtxUsed;
    int                  _prevLen;
    int64_t              _responseGenerationTime;
    int                  _responseNumTokens;

  public:
    void loadModel(const char* modelPath, const InferenceParams& params);

    void addChatMessage(const char* message, const char* role);

    float getResponseGenerationTime() const;

    int getContextSizeUsed() const;

    void startCompletion(const char* query);

    std::string completionLoop();

    void stopCompletion();

    ~LLMInference();
};