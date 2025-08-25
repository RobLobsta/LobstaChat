package com.roblobsta.lobstachat.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey val id: Int = 1,
    val defaultModelId: String?,
    val theme: String,
    val language: String,
)
