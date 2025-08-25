package com.roblobsta.lobstachat.data.repositories

import com.roblobsta.lobstachat.data.daos.TaskDao
import com.roblobsta.lobstachat.data.models.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    suspend fun addTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
}

class TaskRepositoryImpl(private val taskDao: TaskDao) : TaskRepository {
    override fun getAllTasks(): Flow<List<Task>> = taskDao.getAll()
    override suspend fun addTask(task: Task) = taskDao.insert(task)
    override suspend fun updateTask(task: Task) = taskDao.update(task)
    override suspend fun deleteTask(task: Task) = taskDao.delete(task)
}
