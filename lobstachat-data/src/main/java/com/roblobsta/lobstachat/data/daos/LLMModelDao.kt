package com.roblobsta.lobstachat.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.roblobsta.lobstachat.data.models.LLMModel
import kotlinx.coroutines.flow.Flow

@Dao
interface LLMModelDao {
    @Query("SELECT * FROM llm_models")
    fun getAll(): Flow<List<LLMModel>>

    @Query("SELECT * FROM llm_models WHERE isDownloaded = 1")
    fun getDownloaded(): Flow<List<LLMModel>>

    @Query("SELECT * FROM llm_models WHERE id = :id")
    suspend fun getById(id: String): LLMModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(model: LLMModel)

    @Update
    suspend fun update(model: LLMModel)

    @Query("DELETE FROM llm_models WHERE id = :id")
    suspend fun deleteById(id: String)
}
