package com.roblobsta.lobstachat.data.repositories

import com.roblobsta.lobstachat.data.daos.FolderDao
import com.roblobsta.lobstachat.data.models.Folder
import kotlinx.coroutines.flow.Flow

interface FolderRepository {
    fun getAllFolders(): Flow<List<Folder>>
    suspend fun addFolder(folder: Folder)
    suspend fun updateFolder(folder: Folder)
    suspend fun deleteFolder(folder: Folder)
}

class FolderRepositoryImpl(private val folderDao: FolderDao) : FolderRepository {
    override fun getAllFolders(): Flow<List<Folder>> = folderDao.getAll()
    override suspend fun addFolder(folder: Folder) = folderDao.insert(folder)
    override suspend fun updateFolder(folder: Folder) = folderDao.update(folder)
    override suspend fun deleteFolder(folder: Folder) = folderDao.delete(folder)
}
