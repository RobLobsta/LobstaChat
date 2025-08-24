package com.roblobsta.lobstachat.lm

interface InferenceParams {
    val minP: Float
    val temperature: Float
    val nThreads: Int
    val useMmap: Boolean
    val useMlock: Boolean
    val topK: Int
    val topP: Float
    val xtcP: Float
    val xtcT: Float
    val storeChats: Boolean
    val contextSize: Long?
    val chatTemplate: String?
}
