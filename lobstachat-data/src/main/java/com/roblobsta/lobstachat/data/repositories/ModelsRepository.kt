package com.roblobsta.lobstachat.data.repositories

import com.roblobsta.lobstachat.data.daos.LLMModelDao
import com.roblobsta.lobstachat.data.models.LLMModel
import kotlinx.coroutines.flow.Flow

interface ModelsRepository {
    fun getAllModels(): Flow<List<LLMModel>>
    fun getDownloadedModels(): Flow<List<LLMModel>>
    suspend fun getModelById(id: String): LLMModel?
    suspend fun addModel(model: LLMModel)
    suspend fun updateModel(model: LLMModel)
    suspend fun deleteModel(id: String)
}

class ModelsRepositoryImpl(private val llmModelDao: LLMModelDao) : ModelsRepository {
    override fun getAllModels(): Flow<List<LLMModel>> = llmModelDao.getAll()
    override fun getDownloadedModels(): Flow<List<LLMModel>> = llmModelDao.getDownloaded()
    override suspend fun getModelById(id: String): LLMModel? = llmModelDao.getById(id)
    override suspend fun addModel(model: LLMModel) = llmModelDao.insert(model)
    override suspend fun updateModel(model: LLMModel) = llmModelDao.update(model)
    override suspend fun deleteModel(id: String) = llmModelDao.deleteById(id)
}
