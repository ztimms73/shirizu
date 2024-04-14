package org.xtimms.shirizu.sections.settings.backup

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.xtimms.shirizu.core.components.PreferenceItemDescription
import org.xtimms.shirizu.core.components.PreferenceItemTitle
import org.xtimms.shirizu.ui.theme.ShirizuTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BackupItem(
    title: String,
    enabled: Boolean = true,
    isChecked: Boolean = true,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = Modifier.combinedClickable(
            onClick = onClick,
            enabled = enabled,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                modifier = Modifier.padding(start = 8.dp),
                checked = isChecked,
                onCheckedChange = null
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp)
            ) {
                PreferenceItemTitle(text = title, enabled = enabled)
            }
        }
    }
}

@Preview
@Composable
fun BackupItemPreview() {
    ShirizuTheme {
        BackupItem(title = "Title")
    }
}