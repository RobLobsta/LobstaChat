package com.roblobsta.lobstachat.data

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "Persona")
data class Persona(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String = "",
    var systemPrompt: String = "",
    var modelId: Long = -1,
    var shortcutId: String? = null,
    @Embedded
    var inferenceParams: InferenceParamsData = InferenceParamsData(),
    @Transient var modelName: String = "",
)

@Dao
interface PersonaDao {
    @Query("SELECT * FROM Persona WHERE id = :personaId")
    suspend fun getPersona(personaId: Long): Persona

    @Query("SELECT * FROM Persona")
    fun getPersonas(): Flow<List<Persona>>

    @Insert
    suspend fun insertPersona(persona: Persona)

    @Update
    suspend fun updatePersona(persona: Persona)

    @Query("DELETE FROM Persona WHERE id = :personaId")
    suspend fun deletePersona(personaId: Long)
}
