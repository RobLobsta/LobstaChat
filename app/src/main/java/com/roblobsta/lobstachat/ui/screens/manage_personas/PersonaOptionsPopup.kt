package com.roblobsta.lobstachat.ui.screens.manage_personas

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import compose.icons.FeatherIcons
import compose.icons.feathericons.Delete
import compose.icons.feathericons.Edit
import compose.icons.feathericons.Plus
import com.roblobsta.lobstachat.R

@Composable
fun PersonaOptionsPopup(
    isShortcutPinned: Boolean,
    onDismiss: () -> Unit,
    onDeletePersonaClick: () -> Unit,
    onEditPersonaClick: () -> Unit,
    onAddPersonaShortcut: () -> Unit,
    onRemovePersonaShortcut: () -> Unit,
) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = { onDismiss() },
    ) {
        DropdownMenuItem(
            leadingIcon = { Icon(FeatherIcons.Edit, contentDescription = "Edit Persona") },
            text = { Text(stringResource(R.string.persona_popup_edit_persona)) },
            onClick = { onEditPersonaClick() },
        )
        DropdownMenuItem(
            leadingIcon = { Icon(FeatherIcons.Delete, contentDescription = "Delete Persona") },
            text = { Text(stringResource(R.string.persona_popup_delete_persona)) },
            onClick = { onDeletePersonaClick() },
        )
        if (isShortcutPinned) {
            DropdownMenuItem(
                leadingIcon = { Icon(FeatherIcons.Delete, contentDescription = "Remove Persona Shortcut") },
                text = { Text(stringResource(R.string.persona_options_remove_shortcut)) },
                onClick = { onRemovePersonaShortcut() },
            )
        } else {
            DropdownMenuItem(
                leadingIcon = { Icon(FeatherIcons.Plus, contentDescription = "Add Persona Shortcut") },
                text = { Text(stringResource(R.string.persona_options_add_shortcut)) },
                onClick = { onAddPersonaShortcut() },
            )
        }
    }
}
