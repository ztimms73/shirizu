package org.xtimms.tokusho.core.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
fun BackIconButton(
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = "arrow_back"
        )
    }
}