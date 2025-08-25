package com.roblobsta.lobstachat.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.roblobsta.lobstachat.data.models.Persona
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonaDao {
    @Query("SELECT * FROM personas")
    fun getAll(): Flow<List<Persona>>

    @Insert
    suspend fun insert(persona: Persona)

    @Update
    suspend fun update(persona: Persona)

    @Delete
    suspend fun delete(persona: Persona)
}
