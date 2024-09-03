package org.xtimms.shirizu.sections.explore.sources

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.ExtensionOff
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koitharu.kotatsu.parsers.model.MangaParserSource
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.ScrollbarLazyColumn
import org.xtimms.shirizu.core.model.isNsfw
import org.xtimms.shirizu.core.ui.screens.EmptyScreen
import org.xtimms.shirizu.core.ui.screens.LoadingScreen
import org.xtimms.shirizu.utils.material.SecondaryItemAlpha
import org.xtimms.shirizu.utils.system.getDisplayName
import org.xtimms.shirizu.utils.system.plus
import org.xtimms.shirizu.utils.system.toLocale

@Composable
fun SourcesScreen(
    state: SourcesScreenModel.State,
    contentPadding: PaddingValues,
    onClickItem: (MangaSource) -> Unit,
    onClickMenu: (MangaSource) -> Unit,
    onClickHide: (MangaSource) -> Unit,
    onLongClickItem: (MangaSource) -> Unit,
) {
    when {
        state.isLoading -> LoadingScreen(Modifier.padding(contentPadding))
        state.isEmpty -> EmptyScreen(
            icon = Icons.Outlined.ExtensionOff,
            title = R.string.no_enabled_sources,
            description = R.string.no_enabled_sources_hint,
            modifier = Modifier.padding(contentPadding),
        )
        else -> {
            ScrollbarLazyColumn(
                contentPadding = contentPadding + PaddingValues(top = 8.dp),
            ) {
                items(
                    items = state.items,
                    contentType = {
                        when (it) {
                            is SourceUiModel.Header -> "sources_header"
                            is SourceUiModel.Item -> "sources_item"
                        }
                    },
                    key = {
                        when (it) {
                            is SourceUiModel.Header -> it.hashCode()
                            is SourceUiModel.Item -> "source-${it.source.ordinal}"
                        }
                    },
                ) { model ->
                    when (model) {
                        is SourceUiModel.Header -> {
                            SourceHeader(
                                modifier = Modifier.animateItem(),
                                language = model.language ?: stringResource(id = R.string.multi_lang),
                            )
                        }
                        is SourceUiModel.Item -> SourceItem(
                            modifier = Modifier.animateItem(),
                            source = model.source,
                            onClickItem = onClickItem,
                            onLongClickItem = onLongClickItem,
                            onClickMenu = onClickMenu,
                            onClickHide = onClickHide
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SourceHeader(
    language: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Text(
        text = language.toLocale().getDisplayName(context),
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        style = MaterialTheme.typography.bodyLarge,
    )
}

@Composable
fun SourceItem(
    source: MangaParserSource,
    onClickItem: (MangaSource) -> Unit,
    onLongClickItem: (MangaSource) -> Unit,
    onClickMenu: (MangaSource) -> Unit,
    onClickHide: (MangaSource) -> Unit,
    modifier: Modifier = Modifier,
) {
    BaseSourceItem(
        modifier = modifier,
        source = source,
        onClickItem = { onClickItem(source) },
        onLongClickItem = { onLongClickItem(source) },
        action = {
            if (it.isNsfw()) {
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = "18+",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            IconButton(onClick = { onClickHide(it) }) {
                Icon(
                    imageVector = Icons.Outlined.Remove,
                    tint = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = SecondaryItemAlpha,
                    ),
                    contentDescription = stringResource(R.string.remove),
                )
            }
            IconButton(onClick = { onClickMenu(it) }) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    tint = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = SecondaryItemAlpha,
                    ),
                    contentDescription = stringResource(R.string.open_menu),
                )
            }
        },
    )
}

@Composable
private fun SourcePinButton(
    isPinned: Boolean,
    onClick: () -> Unit,
) {
    val icon = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin
    val tint = if (isPinned) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onBackground.copy(
            alpha = SecondaryItemAlpha,
        )
    }
    val description = if (isPinned) R.string.action_unpin else R.string.action_pin
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            tint = tint,
            contentDescription = stringResource(description),
        )
    }
}

sealed interface SourceUiModel {
    data class Item(val source: MangaParserSource) : SourceUiModel
    data class Header(val language: String?) : SourceUiModel
}