package org.xtimms.tokusho.sections.settings.shelf.categories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NewLabel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.components.ConfirmButton
import org.xtimms.tokusho.core.components.DismissButton

@Composable
fun AddCategoryDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(id = R.string.add_category)) },
        icon = { Icon(Icons.Outlined.NewLabel, null) },
        text = {
            Column {
                OutlinedTextField(
                    modifier = Modifier.padding(bottom = 8.dp),
                    value = "",
                    onValueChange = {  },
                    label = {
                        Text(stringResource(id = R.string.name))
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
            }
        }, confirmButton = {
            ConfirmButton {
                onDismissRequest()
            }
        }, dismissButton = {
            DismissButton {
                onDismissRequest()
            }
        })
}