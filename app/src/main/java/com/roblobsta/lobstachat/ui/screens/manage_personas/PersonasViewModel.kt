package com.roblobsta.lobstachat.ui.screens.manage_personas

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roblobsta.lobstachat.data.AppDB
import com.roblobsta.lobstachat.data.Persona
import com.roblobsta.lobstachat.data.InferenceParams
import com.roblobsta.lobstachat.llm.ModelsRepository
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class PersonasViewModel(
    val appDB: AppDB,
    val modelsRepository: ModelsRepository,
) : ViewModel() {
    val showCreatePersonaDialogState = mutableStateOf(false)
    val showEditPersonaDialogState = mutableStateOf(false)
    val selectedPersonaState = mutableStateOf<Persona?>(null)

    fun deletePersona(personaId: Long) {
        viewModelScope.launch { appDB.deletePersona(personaId) }
    }

    fun updatePersona(persona: Persona) {
        viewModelScope.launch { appDB.updatePersona(persona) }
    }

    fun addPersona(
        name: String,
        systemPrompt: String,
        modelId: Long,
        inferenceParams: InferenceParams
    ) {
        viewModelScope.launch { appDB.addPersona(name, systemPrompt, modelId, inferenceParams) }
    }
}
