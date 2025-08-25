package com.roblobsta.lobstachat.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chatId: Int,
    val isUser: Boolean,
    val content: String,
    val timestamp: Date,
    val generationSpeed: Float? = null,
    val responseTimeSecs: Int? = null,
)
