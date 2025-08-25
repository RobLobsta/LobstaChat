package com.roblobsta.lobstachat.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.roblobsta.lobstachat.data.models.Chat
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats")
    fun getAll(): Flow<List<Chat>>

    @Query("SELECT * FROM chats WHERE id = :id")
    suspend fun getById(id: Int): Chat?

    @Insert
    suspend fun insert(chat: Chat): Long

    @Update
    suspend fun update(chat: Chat)

    @Delete
    suspend fun delete(chat: Chat)

    @Query("SELECT COUNT(*) FROM chats")
    suspend fun getCount(): Int
}
