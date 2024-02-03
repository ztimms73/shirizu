package org.xtimms.tokusho.core.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsToolbar(
    title: String,
    titleAlphaProvider: () -> Float,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundAlphaProvider: () -> Float = titleAlphaProvider
) {
    Column(
        modifier = modifier,
    ) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = LocalContentColor.current.copy(alpha = titleAlphaProvider()),
                )
            },
            navigationIcon = {
                BackIconButton(
                    onClick = onBackClicked
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme
                    .surfaceColorAtElevation(3.dp)
                    .copy(alpha = backgroundAlphaProvider())
            )
        )
    }
}