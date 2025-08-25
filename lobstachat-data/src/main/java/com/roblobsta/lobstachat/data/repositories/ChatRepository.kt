package com.roblobsta.lobstachat.data.repositories

import com.roblobsta.lobstachat.data.daos.ChatDao
import com.roblobsta.lobstachat.data.daos.ChatMessageDao
import com.roblobsta.lobstachat.data.models.Chat
import com.roblobsta.lobstachat.data.models.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getAllChats(): Flow<List<Chat>>
    suspend fun getChatById(id: Int): Chat?
    suspend fun addChat(chat: Chat): Long
    suspend fun updateChat(chat: Chat)
    suspend fun deleteChat(chat: Chat)
    suspend fun getChatsCount(): Int
    fun getMessagesForChat(chatId: Int): Flow<List<ChatMessage>>
    suspend fun addMessage(message: ChatMessage)
    suspend fun deleteMessage(message: ChatMessage)
    suspend fun deleteMessagesForChat(chatId: Int)
}

class ChatRepositoryImpl(
    private val chatDao: ChatDao,
    private val chatMessageDao: ChatMessageDao,
) : ChatRepository {
    override fun getAllChats(): Flow<List<Chat>> = chatDao.getAll()
    override suspend fun getChatById(id: Int): Chat? = chatDao.getById(id)
    override suspend fun addChat(chat: Chat): Long = chatDao.insert(chat)
    override suspend fun updateChat(chat: Chat) = chatDao.update(chat)
    override suspend fun deleteChat(chat: Chat) = chatDao.delete(chat)
    override suspend fun getChatsCount(): Int = chatDao.getCount()
    override fun getMessagesForChat(chatId: Int): Flow<List<ChatMessage>> =
        chatMessageDao.getMessagesForChat(chatId)
    override suspend fun addMessage(message: ChatMessage) = chatMessageDao.insert(message)
    override suspend fun deleteMessage(message: ChatMessage) = chatMessageDao.delete(message)
    override suspend fun deleteMessagesForChat(chatId: Int) =
        chatMessageDao.deleteMessagesForChat(chatId)
}
