package com.roblobsta.lobstachat.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "chats")
data class Chat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val folderId: Int?,
    val systemPrompt: String,
    val modelId: String,
    val createdAt: Date,
    val lastModifiedAt: Date,
    val temperature: Float,
    val topK: Int,
    val topP: Float,
    val repeatPenalty: Float,
)
