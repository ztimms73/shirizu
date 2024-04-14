package org.xtimms.shirizu.core.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsToolbar(
    title: String,
    titleAlphaProvider: () -> Float,
    navigateBack: () -> Unit,
    navigateToWebBrowser: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundAlphaProvider: () -> Float = titleAlphaProvider
) {

    var expanded by remember { mutableStateOf(false) }

    val padding by animateDpAsState(
        targetValue = if (backgroundAlphaProvider() == 1f) 0.dp else 16.dp,
        label = "padding",
    )

    Column(
        modifier = modifier
    ) {
        TopAppBar(
            navigationIcon = {
                CircleBackIconButton(
                    modifier = Modifier.padding(start = padding),
                    onClick = navigateBack
                )
            },
            title = {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = LocalContentColor.current.copy(alpha = titleAlphaProvider()),
                )
            },
            actions = {
                FilledTonalIconButton(
                    modifier = Modifier.padding(end = padding),
                    onClick = { expanded = true }, colors = IconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = MaterialTheme.colorScheme.outline,
                        disabledContentColor = MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text("Share") },
                        onClick = { /*TODO*/ },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Download") },
                        onClick = { /*TODO*/ },
                        leadingIcon = {
                            Icon(imageVector = Icons.Outlined.Download, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Open in web browser") },
                        onClick = {
                            navigateToWebBrowser()
                            expanded = false
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Outlined.Language, contentDescription = null)
                        }
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme
                    .surfaceColorAtElevation(3.dp)
                    .copy(alpha = backgroundAlphaProvider())
            )
        )
    }
}