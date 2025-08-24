package com.roblobsta.lobstachat.ui.screens.manage_personas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import compose.icons.FeatherIcons
import compose.icons.feathericons.Check
import com.roblobsta.lobstachat.R
import com.roblobsta.lobstachat.data.InferenceParams
import com.roblobsta.lobstachat.ui.screens.chat.SelectModelsList

@Composable
fun EditPersonaDialog(viewModel: PersonasViewModel) {
    val selectedPersona by remember { viewModel.selectedPersonaState }
    selectedPersona?.let { persona ->
        var personaName by remember { mutableStateOf(persona.name) }
        var systemPrompt by remember { mutableStateOf(persona.systemPrompt) }
        var selectedModel by remember {
            mutableStateOf(viewModel.modelsRepository.getModelFromId(persona.modelId))
        }
        var isModelListDialogVisible by remember { mutableStateOf(false) }
        val modelsList = viewModel.modelsRepository.getAvailableModelsList()
        val (focusRequestor) = FocusRequester.createRefs()
        val keyboardController = LocalSoftwareKeyboardController.current
        var temperature by remember { mutableStateOf(persona.inferenceParams.temperature.toString()) }
        var topK by remember { mutableStateOf(persona.inferenceParams.topK.toString()) }
        var topP by remember { mutableStateOf(persona.inferenceParams.topP.toString()) }

        LaunchedEffect(selectedPersona) {
            personaName = persona.name
            systemPrompt = persona.systemPrompt
        }
        var showEditPersonaDialog by remember { viewModel.showEditPersonaDialogState }
        if (showEditPersonaDialog) {
            Surface {
                Dialog(onDismissRequest = {
                    showEditPersonaDialog = false
                }) {
                    Column(
                        modifier =
                            Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(R.string.persona_edit_persona_title),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            value = personaName,
                            onValueChange = { personaName = it },
                            label = { Text(stringResource(R.string.persona_create_persona_name)) },
                            keyboardOptions =
                                KeyboardOptions.Default.copy(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next,
                                ),
                            keyboardActions =
                                KeyboardActions(
                                    onNext = {
                                        focusRequestor.requestFocus()
                                    },
                                ),
                        )

                        TextField(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).focusRequester(focusRequestor),
                            value = systemPrompt,
                            onValueChange = { systemPrompt = it },
                            label = { Text(stringResource(R.string.persona_create_persona_sys_prompt)) },
                            keyboardOptions =
                                KeyboardOptions.Default.copy(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    imeAction = ImeAction.Done,
                                ),
                            keyboardActions =
                                KeyboardActions(
                                    onDone = {
                                        keyboardController?.hide()
                                    },
                                ),
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .border(width = 1.dp, Color.DarkGray)
                                    .clickable { isModelListDialogVisible = true }
                                    .padding(8.dp),
                            text =
                                if (selectedModel == null) {
                                    stringResource(R.string.persona_create_persona_select_model)
                                } else {
                                    selectedModel!!
                                        .name
                                },
                        )

                        if (isModelListDialogVisible) {
                            SelectModelsList(
                                onDismissRequest = { isModelListDialogVisible = false },
                                modelsList = modelsList,
                                onModelListItemClick = { model ->
                                    isModelListDialogVisible = false
                                    selectedModel = model
                                },
                                onModelDeleteClick = { // Not applicable, as showModelDeleteIcon is set to false
                                },
                                showModelDeleteIcon = false,
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = temperature,
                            onValueChange = { temperature = it },
                            label = { Text("Temperature") },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = topK,
                            onValueChange = { topK = it },
                            label = { Text("Top K") },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = topP,
                            onValueChange = { topP = it },
                            label = { Text("Top P") },
                            modifier = Modifier.fillMaxWidth(),
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            enabled = personaName.isNotBlank() && systemPrompt.isNotBlank(),
                            onClick = {
                                selectedModel?.let { model ->
                                    viewModel.updatePersona(
                                        persona.copy(
                                            name = personaName,
                                            systemPrompt = systemPrompt,
                                            modelId = model.id,
                                            modelName = model.name,
                                            inferenceParams = persona.inferenceParams.copy(
                                                temperature = temperature.toFloat(),
                                                topK = topK.toInt(),
                                                topP = topP.toFloat()
                                            )
                                        ),
                                    )
                                }
                                showEditPersonaDialog = false
                            },
                        ) {
                            Icon(FeatherIcons.Check, contentDescription = "Update")
                            Text(stringResource(R.string.persona_edit_persona_update))
                        }
                    }
                }
            }
        }
    }
}
