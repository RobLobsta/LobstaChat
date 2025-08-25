package com.roblobsta.lobstachat.data.models

data class InferenceParams(
    val temperature: Float,
    val topK: Int,
    val topP: Float,
    val repeatPenalty: Float,
)
