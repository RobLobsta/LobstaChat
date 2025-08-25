package com.roblobsta.lobstachat.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val systemPrompt: String,
    val modelId: String,
    @Embedded val inferenceParams: InferenceParams,
    val shortcutId: String? = null,
)
