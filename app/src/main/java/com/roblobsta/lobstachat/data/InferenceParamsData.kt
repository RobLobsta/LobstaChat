package com.roblobsta.lobstachat.data

import com.roblobsta.lobstachat.lm.InferenceParams

data class InferenceParamsData(
    override val minP: Float = 0.1f,
    override val temperature: Float = 0.8f,
    override val nThreads: Int = 4,
    override val useMmap: Boolean = true,
    override val useMlock: Boolean = false,
    override val topK: Int = 40,
    override val topP: Float = 0.9f,
    override val xtcP: Float = 0.0f,
    override val xtcT: Float = 1.0f,
    override val storeChats: Boolean = true,
    override val contextSize: Long? = null,
    override val chatTemplate: String? = null,
) : InferenceParams
