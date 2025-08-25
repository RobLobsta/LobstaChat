package com.roblobsta.lobstachat.data.repositories

import com.roblobsta.lobstachat.data.daos.PersonaDao
import com.roblobsta.lobstachat.data.models.Persona
import kotlinx.coroutines.flow.Flow

interface PersonaRepository {
    fun getAllPersonas(): Flow<List<Persona>>
    suspend fun addPersona(persona: Persona)
    suspend fun updatePersona(persona: Persona)
    suspend fun deletePersona(persona: Persona)
}

class PersonaRepositoryImpl(private val personaDao: PersonaDao) : PersonaRepository {
    override fun getAllPersonas(): Flow<List<Persona>> = personaDao.getAll()
    override suspend fun addPersona(persona: Persona) = personaDao.insert(persona)
    override suspend fun updatePersona(persona: Persona) = personaDao.update(persona)
    override suspend fun deletePersona(persona: Persona) = personaDao.delete(persona)
}
