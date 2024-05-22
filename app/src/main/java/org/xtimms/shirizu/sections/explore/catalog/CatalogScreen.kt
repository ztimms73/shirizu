package org.xtimms.shirizu.sections.explore.catalog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ExploreOff
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.koitharu.kotatsu.parsers.model.ContentType
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.FastScrollLazyColumn
import org.xtimms.shirizu.core.components.FilterSortPanel
import org.xtimms.shirizu.core.components.SearchTextField
import org.xtimms.shirizu.core.model.isNsfw
import org.xtimms.shirizu.core.prefs.AppSettings
import org.xtimms.shirizu.core.prefs.COMICS
import org.xtimms.shirizu.core.prefs.HENTAI
import org.xtimms.shirizu.core.prefs.MANGA
import org.xtimms.shirizu.core.prefs.OTHER
import org.xtimms.shirizu.core.prefs.RELATED
import org.xtimms.shirizu.core.ui.screens.EmptyScreen
import org.xtimms.shirizu.core.ui.screens.LoadingScreen
import org.xtimms.shirizu.sections.explore.sources.BaseSourceItem
import org.xtimms.shirizu.sections.explore.sources.SourceHeader
import org.xtimms.shirizu.sections.explore.sources.SourceUiModel
import org.xtimms.shirizu.utils.material.SecondaryItemAlpha
import org.xtimms.shirizu.utils.system.plus

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CatalogScreen(
    state: CatalogScreenModel.State,
    contentPadding: PaddingValues,
    onFilterChanged: (String) -> Unit,
    onClickItem: (MangaSource) -> Unit,
    onClickMenu: (MangaSource) -> Unit,
    onClickEnable: (MangaSource) -> Unit,
    onLongClickItem: (MangaSource) -> Unit,
    onToggleEnableMangaSources: (Boolean) -> Unit,
    onToggleEnableHentaiSources: (Boolean) -> Unit,
    onToggleEnableComicsSources: (Boolean) -> Unit,
    onToggleEnableOtherSources: (Boolean) -> Unit
) {

    var filterExpanded by remember { mutableStateOf(false) }
    var isMangaContentTypeEnabled by remember { mutableStateOf(AppSettings.isMangaContentTypeEnabled()) }
    var isHentaiContentTypeEnabled by remember { mutableStateOf(AppSettings.isHentaiContentTypeEnabled()) }
    var isComicsContentTypeEnabled by remember { mutableStateOf(AppSettings.isComicsContentTypeEnabled()) }
    var isOtherContentTypeEnabled by remember { mutableStateOf(AppSettings.isOtherContentTypeEnabled()) }

    when {
        state.isLoading -> LoadingScreen(Modifier.padding(contentPadding))
        else -> {
            FastScrollLazyColumn(
                contentPadding = contentPadding + PaddingValues(top = 8.dp),
            ) {
                item {
                    var filter by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(""))
                    }
                    FilterSortPanel(
                        filterIcon = {
                            IconButton(onClick = { filterExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null, // FIXME
                                )
                            }
                        },
                        filterTextField = {
                            SearchTextField(
                                value = filter,
                                onValueChange = { value ->
                                    filter = value
                                    onFilterChanged(value.text)
                                },
                                hint = stringResource(
                                    id = R.string.filter_n_sources,
                                    state.items.size
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                onCleared = {
                                    filter = TextFieldValue()
                                    onFilterChanged("")
                                    filterExpanded = false
                                },
                            )
                        },
                        filterExpanded = filterExpanded,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        FilterChip(
                            selected = isMangaContentTypeEnabled,
                            leadingIcon = {
                                AnimatedVisibility(visible = isMangaContentTypeEnabled) {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = null,
                                    )
                                }
                            },
                            onClick = {
                                isMangaContentTypeEnabled = !isMangaContentTypeEnabled
                                AppSettings.updateValue(MANGA, isMangaContentTypeEnabled)
                                onToggleEnableMangaSources(isMangaContentTypeEnabled)
                            },
                            label = {
                                Text(text = stringResource(id = R.string.manga))
                            },
                        )

                        FilterChip(
                            selected = isHentaiContentTypeEnabled,
                            leadingIcon = {
                                AnimatedVisibility(visible = isHentaiContentTypeEnabled) {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = null,
                                    )
                                }
                            },
                            onClick = {
                                isHentaiContentTypeEnabled = !isHentaiContentTypeEnabled
                                AppSettings.updateValue(HENTAI, isHentaiContentTypeEnabled)
                                onToggleEnableHentaiSources(isHentaiContentTypeEnabled)
                            },
                            label = {
                                Text(text = stringResource(id = R.string.hentai))
                            },
                        )

                        FilterChip(
                            selected = isComicsContentTypeEnabled,
                            leadingIcon = {
                                AnimatedVisibility(visible = isComicsContentTypeEnabled) {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = null,
                                    )
                                }
                            },
                            onClick = {
                                isComicsContentTypeEnabled = !isComicsContentTypeEnabled
                                AppSettings.updateValue(COMICS, isComicsContentTypeEnabled)
                                onToggleEnableComicsSources(isComicsContentTypeEnabled)
                            },
                            label = {
                                Text(text = stringResource(id = R.string.comics))
                            },
                        )

                        FilterChip(
                            selected = isOtherContentTypeEnabled,
                            leadingIcon = {
                                AnimatedVisibility(visible = isOtherContentTypeEnabled) {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = null,
                                    )
                                }
                            },
                            onClick = {
                                isOtherContentTypeEnabled = !isOtherContentTypeEnabled
                                AppSettings.updateValue(OTHER, isOtherContentTypeEnabled)
                                onToggleEnableOtherSources(isOtherContentTypeEnabled)
                            },
                            label = {
                                Text(text = stringResource(id = R.string.other_source))
                            },
                        )
                    }
                }
                items(
                    items = state.items,
                    contentType = {
                        when (it) {
                            is SourceUiModel.Header -> "catalog_header"
                            is SourceUiModel.Item -> "catalog_item"
                        }
                    },
                    key = {
                        when (it) {
                            is SourceUiModel.Header -> it.hashCode()
                            is SourceUiModel.Item -> "catalog_source-${it.source.ordinal}-${it.source.name}"
                        }
                    },
                ) { model ->
                    when (model) {
                        is SourceUiModel.Header -> {
                            SourceHeader(
                                modifier = Modifier.animateItem(),
                                language = model.language
                                    ?: stringResource(id = R.string.multi_lang),
                            )
                        }

                        is SourceUiModel.Item -> SourceItem(
                            modifier = Modifier.animateItem(),
                            source = model.source,
                            onClickItem = onClickItem,
                            onLongClickItem = onLongClickItem,
                            onClickMenu = onClickMenu,
                            onClickEnable = onClickEnable
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SourceItem(
    source: MangaSource,
    onClickItem: (MangaSource) -> Unit,
    onLongClickItem: (MangaSource) -> Unit,
    onClickMenu: (MangaSource) -> Unit,
    onClickEnable: (MangaSource) -> Unit,
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
            IconButton(onClick = { onClickEnable(it) }) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    tint = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = SecondaryItemAlpha,
                    ),
                    contentDescription = stringResource(R.string.add),
                )
            }
        },
    )
}