package com.roblobsta.lobstachat.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "llm_models")
data class LLMModel(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val author: String,
    val isDownloaded: Boolean,
    val downloadUrl: String?,
    val filePath: String?,
    val fileSize: Long,
    val requiredRam: Long,
    val isFromHuggingFace: Boolean,
    val hfModelId: String?,
    val hfGGUFRepo: String?,
    val hfGGUFFile: String?,
)
