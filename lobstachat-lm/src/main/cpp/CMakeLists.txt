cmake_minimum_required(VERSION 3.22.1)
project(lobstachatlm)

# Resolve LLAMA_DIR to an absolute path
set(LLAMA_DIR_RELATIVE "../../../../llama.cpp")
get_filename_component(LLAMA_DIR ${LLAMA_DIR_RELATIVE} ABSOLUTE)

# Disable llama.cpp options that are not needed
set(LLAMA_BUILD_TESTS OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_SERVER OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_LIB_SHARED OFF CACHE BOOL "" FORCE)

# Disable llamafile which causes issues with armeabi-v7a
set(GGML_LLAMAFILE OFF CACHE BOOL "" FORCE)

add_subdirectory(${LLAMA_DIR} llama.cpp)

set(LOBSTACHATLM_SOURCES
        LLMInference.cpp
        lobstachatlm.cpp
        )
set(GGUF_READER_SOURCES
        GGUFReader.cpp
        )

add_compile_options("-ffile-prefix-map=${LLAMA_DIR}=.")
add_link_options("LINKER:--build-id=none")

function(build_library target_name)
    add_library(
            ${target_name}
            SHARED
            ${LOBSTACHATLM_SOURCES}
    )
    target_include_directories(
            ${target_name}
            PUBLIC
            ${LLAMA_DIR}/include
            ${LLAMA_DIR}/common
    )
    target_link_libraries(
            ${target_name}
            PRIVATE
            llama
            android
            log
    )
endfunction()

build_library(lobstachatlm)

# library target for GGUFReader
set(TARGET_NAME_GGUF_READER ggufreader)
add_library(${TARGET_NAME_GGUF_READER} SHARED ${GGUF_READER_SOURCES})
target_include_directories(
        ${TARGET_NAME_GGUF_READER}
        PUBLIC
        ${LLAMA_DIR}/include
)
target_link_libraries(
        ${TARGET_NAME_GGUF_READER}
        PRIVATE
        llama
)