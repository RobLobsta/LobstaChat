package com.roblobsta.lobstachat.ui.screens.manage_personas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.Check
import compose.icons.feathericons.MoreVertical
import com.roblobsta.lobstachat.R
import com.roblobsta.lobstachat.data.Persona
import com.roblobsta.lobstachat.ui.components.AppAlertDialog
import com.roblobsta.lobstachat.ui.components.AppBarTitleText
import com.roblobsta.lobstachat.ui.components.LargeLabelText
import com.roblobsta.lobstachat.ui.components.createAlertDialog
import com.roblobsta.lobstachat.ui.screens.chat.ChatActivity
import com.roblobsta.lobstachat.ui.theme.LobstaChatTheme
import org.koin.androidx.compose.koinViewModel

class ManagePersonasActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PersonasActivityScreenUI() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonasActivityScreenUI() {
    val viewModel: PersonasViewModel = koinViewModel()
    val context = LocalContext.current
    LobstaChatTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { AppBarTitleText(text = stringResource(R.string.personas_manage_personas_title)) },
                    actions = {
                        IconButton(
                            onClick = { viewModel.showCreatePersonaDialogState.value = true },
                        ) {
                            Icon(FeatherIcons.Check, contentDescription = "Add New Persona")
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { (context as ManagePersonasActivity).finish() }) {
                            Icon(
                                FeatherIcons.ArrowLeft,
                                contentDescription = "Navigate Back",
                            )
                        }
                    },
                )
            },
        ) { paddingValues ->
            Column(
                modifier =
                    Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .fillMaxSize()
                        .padding(paddingValues),
            ) {
                val personas by viewModel.appDB.getPersonas().collectAsState(emptyList())
                Text(
                    text = stringResource(R.string.personas_manage_personas_desc),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(16.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                PersonasList(
                    personas.map {
                        val modelName =
                            viewModel.modelsRepository.getModelFromId(it.modelId)?.name
                                ?: return@map it
                        it.copy(modelName = modelName)
                    },
                    onPersonaSelected = { /* Not applicable as enablePersonaClick is set to `false` */ },
                    onUpdatePersonaClick = { persona ->
                        viewModel.updatePersona(persona)
                    },
                    onEditPersonaClick = { persona ->
                        viewModel.selectedPersonaState.value = persona
                        viewModel.showEditPersonaDialogState.value = true
                    },
                    onDeletePersonaClick = { persona ->
                        createAlertDialog(
                            dialogTitle = context.getString(R.string.dialog_delete_persona_title),
                            dialogText = "Are you sure you want to delete persona '${persona.name}'?",
                            dialogPositiveButtonText = context.getString(R.string.dialog_pos_delete),
                            dialogNegativeButtonText = context.getString(R.string.dialog_neg_cancel),
                            onPositiveButtonClick = {
                                viewModel.deletePersona(persona.id)
                                Toast
                                    .makeText(
                                        context,
                                        "Persona '${persona.name}' deleted",
                                        Toast.LENGTH_LONG,
                                    ).show()
                            },
                            onNegativeButtonClick = {},
                        )
                    },
                    enablePersonaClick = false,
                    showPersonaOptions = true,
                )

                CreatePersonaDialog(viewModel)
                EditPersonaDialog(viewModel)
                AppAlertDialog()
            }
        }
    }
}

@Composable
fun PersonasList(
    personas: List<Persona>,
    onPersonaSelected: (Persona) -> Unit,
    onEditPersonaClick: (Persona) -> Unit,
    onDeletePersonaClick: (Persona) -> Unit,
    onUpdatePersonaClick: (Persona) -> Unit,
    enablePersonaClick: Boolean,
    showPersonaOptions: Boolean,
) {
    LazyColumn {
        items(personas) { persona ->
            PersonaItem(
                persona,
                onPersonaSelected = { onPersonaSelected(persona) },
                onDeletePersonaClick = { onDeletePersonaClick(persona) },
                onEditPersonaClick = { onEditPersonaClick(persona) },
                onUpdatePersona = { onUpdatePersonaClick(it) },
                enablePersonaClick,
                showPersonaOptions,
            )
        }
    }
}

@Composable
private fun PersonaItem(
    persona: Persona,
    onPersonaSelected: () -> Unit,
    onDeletePersonaClick: () -> Unit,
    onEditPersonaClick: () -> Unit,
    onUpdatePersona: (Persona) -> Unit,
    enablePersonaClick: Boolean = false,
    showPersonaOptions: Boolean = true,
) {
    Row(
        modifier =
            if (enablePersonaClick) {
                Modifier
                    .fillMaxWidth()
                    .clickable { onPersonaSelected() }
            } else {
                Modifier.fillMaxWidth()
            }.background(MaterialTheme.colorScheme.surfaceContainerHighest),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        val context = LocalContext.current
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .padding(8.dp),
        ) {
            LargeLabelText(text = persona.name)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = persona.systemPrompt,
                style = MaterialTheme.typography.labelSmall,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = persona.modelName,
                style = MaterialTheme.typography.labelSmall,
            )
        }
        if (showPersonaOptions) {
            Box {
                var showPersonaOptionsPopup by remember { mutableStateOf(false) }
                IconButton(
                    onClick = { showPersonaOptionsPopup = true },
                ) {
                    Icon(
                        FeatherIcons.MoreVertical,
                        contentDescription = "More Options",
                    )
                }
                if (showPersonaOptionsPopup) {
                    PersonaOptionsPopup(
                        persona.shortcutId != null,
                        onDismiss = { showPersonaOptionsPopup = false },
                        onDeletePersonaClick = {
                            persona.shortcutId?.let {
                                ShortcutManagerCompat.removeDynamicShortcuts(context, listOf(it))
                                onUpdatePersona(persona.copy(shortcutId = null))
                                Toast.makeText(context, "Shortcut for persona '${persona.name}' removed", Toast.LENGTH_LONG).show()
                            }
                            onDeletePersonaClick()
                            showPersonaOptionsPopup = false
                        },
                        onEditPersonaClick = {
                            onEditPersonaClick()
                            showPersonaOptionsPopup = false
                        },
                        onAddPersonaShortcut = {
                            val shortcut =
                                ShortcutInfoCompat
                                    .Builder(context, "${persona.id}")
                                    .setShortLabel(persona.name)
                                    .setIcon(IconCompat.createWithResource(context, R.drawable.task_shortcut_icon))
                                    .setIntent(
                                        Intent(context, ChatActivity::class.java).apply {
                                            action = Intent.ACTION_VIEW
                                            putExtra("persona_id", persona.id)
                                        },
                                    ).build()
                            ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
                            Toast.makeText(context, "Shortcut for persona '${persona.name}' added", Toast.LENGTH_LONG).show()
                            onUpdatePersona(persona.copy(shortcutId = shortcut.id))
                            showPersonaOptionsPopup = false
                        },
                        onRemovePersonaShortcut = {
                            persona.shortcutId?.let {
                                ShortcutManagerCompat.removeDynamicShortcuts(context, listOf(it))
                                onUpdatePersona(persona.copy(shortcutId = null))
                                Toast.makeText(context, "Shortcut for persona '${persona.name}' removed", Toast.LENGTH_LONG).show()
                            }
                            showPersonaOptionsPopup = false
                        },
                    )
                }
            }
        }
    }
}
