package com.roblobsta.lobstachat.data.repositories

import com.roblobsta.lobstachat.data.daos.SettingsDao
import com.roblobsta.lobstachat.data.models.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<Settings?>
    suspend fun saveSettings(settings: Settings)
}

class SettingsRepositoryImpl(private val settingsDao: SettingsDao) : SettingsRepository {
    override fun getSettings(): Flow<Settings?> = settingsDao.getSettings()
    override suspend fun saveSettings(settings: Settings) = settingsDao.saveSettings(settings)
}
