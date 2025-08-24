package com.roblobsta.lobstachat.data

import android.app.Application
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Single
import java.util.Date

val MIGRATION_1_2 =
    object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE Chat ADD COLUMN topK INTEGER NOT NULL DEFAULT 40")
            database.execSQL("ALTER TABLE Chat ADD COLUMN topP REAL NOT NULL DEFAULT 0.9")
            database.execSQL("ALTER TABLE Chat ADD COLUMN xtcP REAL NOT NULL DEFAULT 0.0")
            database.execSQL("ALTER TABLE Chat ADD COLUMN xtcT REAL NOT NULL DEFAULT 1.0")
        }
    }

val MIGRATION_2_3 =
    object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE Chat ADD COLUMN sessionPath TEXT")
        }
    }

val MIGRATION_4_5 =
    object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `Persona` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `systemPrompt` TEXT NOT NULL, `modelId` INTEGER NOT NULL, `shortcutId` TEXT, `temperature` REAL NOT NULL, `topK` INTEGER NOT NULL, `topP` REAL NOT NULL, `minP` REAL NOT NULL, `xtcP` REAL NOT NULL, `xtcT` REAL NOT NULL)")
        }
    }

@Database(
    entities = [Chat::class, ChatMessage::class, LLMModel::class, Task::class, Folder::class, Persona::class],
    version = 5,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun chatsDao(): ChatsDao

    abstract fun chatMessagesDao(): ChatMessageDao

    abstract fun llmModelDao(): LLMModelDao

    abstract fun taskDao(): TaskDao

    abstract fun personaDao(): PersonaDao

    abstract fun folderDao(): FolderDao
}

import com.roblobsta.lobstachat.R

@Single
class AppDB(
    private val application: Application,
) {
    private val db =
        Room
            .databaseBuilder(
                application,
                AppRoomDatabase::class.java,
                "app-database",
            ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_4_5)
            .build()

    /** Get all chats from the database sorted by dateUsed in descending order. */
    fun getChats(): Flow<List<Chat>> = db.chatsDao().getChats()

    fun searchChats(query: String): Flow<List<Chat>> = db.chatsDao().searchChats("%$query%")

    suspend fun loadDefaultChat(): Chat {
        return getRecentlyUsedChat() ?: addChat(application.getString(R.string.default_chat_name))
    }

    /**
     * Get the most recently used chat from the database. This function might return null, if there
     * are no chats in the database.
     */
    suspend fun getRecentlyUsedChat(): Chat? =
        withContext(Dispatchers.IO) {
            db.chatsDao().getRecentlyUsedChat()
        }

    /**
     * Adds a new chat to the database initialized with given
     * arguments and returns the new Chat object
     */
    suspend fun addChat(
        chatName: String,
        chatTemplate: String = "",
        systemPrompt: String = application.getString(R.string.default_system_prompt),
        llmModelId: Long = -1,
        isTask: Boolean = false,
    ): Chat =
        withContext(Dispatchers.IO) {
            val newChat =
                Chat(
                    name = chatName,
                    systemPrompt = systemPrompt,
                    dateCreated = Date(),
                    dateUsed = Date(),
                    llmModelId = llmModelId,
                    contextSize = 2048,
                    chatTemplate = chatTemplate,
                    isTask = isTask,
                )
            val newChatId = db.chatsDao().insertChat(newChat)
            newChat.copy(id = newChatId)
        }

    /** Update the chat in the database. */
    suspend fun updateChat(modifiedChat: Chat) =
        withContext(Dispatchers.IO) {
            db.chatsDao().updateChat(modifiedChat)
        }

    suspend fun deleteChat(chat: Chat) =
        withContext(Dispatchers.IO) {
            db.chatsDao().deleteChat(chat.id)
        }

    suspend fun getChatsCount(): Long =
        withContext(Dispatchers.IO) {
            db.chatsDao().getChatsCount()
        }

    fun getChatsForFolder(folderId: Long): Flow<List<Chat>> = db.chatsDao().getChatsForFolder(folderId)

    // Chat Messages

    fun getMessages(chatId: Long): Flow<List<ChatMessage>> = db.chatMessagesDao().getMessages(chatId)

    suspend fun getMessagesForModel(chatId: Long): List<ChatMessage> =
        withContext(Dispatchers.IO) {
            db.chatMessagesDao().getMessagesForModel(chatId)
        }

    suspend fun addUserMessage(
        chatId: Long,
        message: String,
    ) = withContext(Dispatchers.IO) {
        db
            .chatMessagesDao()
            .insertMessage(ChatMessage(chatId = chatId, message = message, isUserMessage = true))
    }

    suspend fun addAssistantMessage(
        chatId: Long,
        message: String,
    ) = withContext(Dispatchers.IO) {
        db
            .chatMessagesDao()
            .insertMessage(ChatMessage(chatId = chatId, message = message, isUserMessage = false))
    }

    suspend fun deleteMessage(messageId: Long) =
        withContext(Dispatchers.IO) {
            db.chatMessagesDao().deleteMessage(messageId)
        }

    suspend fun deleteMessages(chatId: Long) =
        withContext(Dispatchers.IO) {
            db.chatMessagesDao().deleteMessages(chatId)
        }

    // Models

    suspend fun addModel(
        name: String,
        url: String,
        path: String,
        contextSize: Int,
        chatTemplate: String,
    ) = withContext(Dispatchers.IO) {
        db.llmModelDao().insertModels(
            LLMModel(
                name = name,
                url = url,
                path = path,
                contextSize = contextSize,
                chatTemplate = chatTemplate,
            ),
        )
    }

    suspend fun getModel(id: Long): LLMModel? =
        withContext(Dispatchers.IO) {
            try {
                db.llmModelDao().getModel(id)
            } catch (_: IllegalArgumentException) {
                null
            }
        }

    fun getModels(): Flow<List<LLMModel>> = db.llmModelDao().getAllModels()

    suspend fun getModelsList(): List<LLMModel> = withContext(Dispatchers.IO) { db.llmModelDao().getAllModelsList() }

    suspend fun deleteModel(id: Long) =
        withContext(Dispatchers.IO) {
            db.llmModelDao().deleteModel(id)
        }

    // Tasks

    suspend fun getTask(taskId: Long): Task =
        withContext(Dispatchers.IO) {
            db.taskDao().getTask(taskId)
        }

    fun getTasks(): Flow<List<Task>> = db.taskDao().getTasks()

    suspend fun addTask(
        name: String,
        systemPrompt: String,
        modelId: Long,
    ) = withContext(Dispatchers.IO) {
        db.taskDao().insertTask(Task(name = name, systemPrompt = systemPrompt, modelId = modelId))
    }

    suspend fun deleteTask(taskId: Long) =
        withContext(Dispatchers.IO) {
            db.taskDao().deleteTask(taskId)
        }

    suspend fun updateTask(task: Task) =
        withContext(Dispatchers.IO) {
            db.taskDao().updateTask(task)
        }

    // Personas

    suspend fun getPersona(personaId: Long): Persona =
        withContext(Dispatchers.IO) {
            db.personaDao().getPersona(personaId)
        }

    fun getPersonas(): Flow<List<Persona>> = db.personaDao().getPersonas()

    suspend fun addPersona(
        name: String,
        systemPrompt: String,
        modelId: Long,
        inferenceParams: InferenceParams
    ) = withContext(Dispatchers.IO) {
        db.personaDao().insertPersona(Persona(name = name, systemPrompt = systemPrompt, modelId = modelId, inferenceParams = inferenceParams))
    }

    suspend fun deletePersona(personaId: Long) =
        withContext(Dispatchers.IO) {
            db.personaDao().deletePersona(personaId)
        }

    suspend fun updatePersona(persona: Persona) =
        withContext(Dispatchers.IO) {
            db.personaDao().updatePersona(persona)
        }

    // Folders

    fun getFolders(): Flow<List<Folder>> = db.folderDao().getFolders()

    suspend fun addFolder(folderName: String) =
        withContext(Dispatchers.IO) {
            db.folderDao().insertFolder(Folder(name = folderName))
        }

    suspend fun updateFolder(folder: Folder) =
        withContext(Dispatchers.IO) {
            db.folderDao().updateFolder(folder)
        }

    /**
     * Deletes the folder from the Folder table only
     */
    suspend fun deleteFolder(folderId: Long) {
        withContext(Dispatchers.IO) {
            db.folderDao().deleteFolder(folderId)
            db.chatsDao().updateFolderIds(folderId, -1L)
        }
    }

    /**
     * Deletes the folder from the Folder table
     * and corresponding chats from the Chat table
     */
    suspend fun deleteFolderWithChats(folderId: Long) {
        withContext(Dispatchers.IO) {
            db.folderDao().deleteFolder(folderId)
            db.chatsDao().deleteChatsInFolder(folderId)
        }
    }
}
