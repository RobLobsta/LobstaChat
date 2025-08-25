package com.roblobsta.lobstachat.data.di

import androidx.room.Room
import com.roblobsta.lobstachat.data.AppDB
import com.roblobsta.lobstachat.data.repositories.ChatRepository
import com.roblobsta.lobstachat.data.repositories.ChatRepositoryImpl
import com.roblobsta.lobstachat.data.repositories.FolderRepository
import com.roblobsta.lobstachat.data.repositories.FolderRepositoryImpl
import com.roblobsta.lobstachat.data.repositories.ModelsRepository
import com.roblobsta.lobstachat.data.repositories.ModelsRepositoryImpl
import com.roblobsta.lobstachat.data.repositories.PersonaRepository
import com.roblobsta.lobstachat.data.repositories.PersonaRepositoryImpl
import com.roblobsta.lobstachat.data.repositories.SettingsRepository
import com.roblobsta.lobstachat.data.repositories.SettingsRepositoryImpl
import com.roblobsta.lobstachat.data.repositories.TaskRepository
import com.roblobsta.lobstachat.data.repositories.TaskRepositoryImpl
import org.koin.dsl.module

val dataModule = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDB::class.java, "lobstachat-db"
        ).build()
    }
    single { get<AppDB>().chatDao() }
    single { get<AppDB>().chatMessageDao() }
    single { get<AppDB>().folderDao() }
    single { get<AppDB>().llmModelDao() }
    single { get<AppDB>().personaDao() }
    single { get<AppDB>().settingsDao() }
    single { get<AppDB>().taskDao() }

    single<ChatRepository> { ChatRepositoryImpl(get(), get()) }
    single<FolderRepository> { FolderRepositoryImpl(get()) }
    single<ModelsRepository> { ModelsRepositoryImpl(get()) }
    single<PersonaRepository> { PersonaRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<TaskRepository> { TaskRepositoryImpl(get()) }
}
