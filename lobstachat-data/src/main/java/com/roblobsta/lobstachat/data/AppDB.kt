package com.roblobsta.lobstachat.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.roblobsta.lobstachat.data.daos.ChatDao
import com.roblobsta.lobstachat.data.daos.ChatMessageDao
import com.roblobsta.lobstachat.data.daos.FolderDao
import com.roblobsta.lobstachat.data.daos.LLMModelDao
import com.roblobsta.lobstachat.data.daos.PersonaDao
import com.roblobsta.lobstachat.data.daos.SettingsDao
import com.roblobsta.lobstachat.data.daos.TaskDao
import com.roblobsta.lobstachat.data.models.Chat
import com.roblobsta.lobstachat.data.models.ChatMessage
import com.roblobsta.lobstachat.data.models.Folder
import com.roblobsta.lobstachat.data.models.LLMModel
import com.roblobsta.lobstachat.data.models.Persona
import com.roblobsta.lobstachat.data.models.Settings
import com.roblobsta.lobstachat.data.models.Task

@Database(
    entities = [
        Chat::class,
        ChatMessage::class,
        Folder::class,
        LLMModel::class,
        Persona::class,
        Settings::class,
        Task::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDB : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun folderDao(): FolderDao
    abstract fun llmModelDao(): LLMModelDao
    abstract fun personaDao(): PersonaDao
    abstract fun settingsDao(): SettingsDao
    abstract fun taskDao(): TaskDao
}
