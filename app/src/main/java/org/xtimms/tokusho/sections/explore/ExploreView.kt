package org.xtimms.tokusho.sections.explore

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.SdStorage
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.AsyncImageImpl
import org.xtimms.tokusho.core.components.ExploreButton
import org.xtimms.tokusho.core.components.SourceItem
import org.xtimms.tokusho.core.components.icons.Dice
import org.xtimms.tokusho.ui.theme.TokushoTheme

const val EXPLORE_DESTINATION = "explore"

@Composable
fun ExploreView(
    viewModel: ExploreViewModel = hiltViewModel(),
    coil: ImageLoader,
    navigateToDetails: (Long) -> Unit,
    navigateToSource: (SourceItemModel) -> Unit,
    navigateToSuggestions: () -> Unit,
    nestedScrollConnection: NestedScrollConnection? = null,
    topBarHeightPx: Float = 0f,
    topBarOffsetY: Animatable<Float, AnimationVector1D> = Animatable(0f),
    listState: LazyGridState,
    padding: PaddingValues = PaddingValues(),
) {

    val context = LocalContext.current
    val layoutDirection = LocalLayoutDirection.current

    val sources = viewModel.content.collectAsStateWithLifecycle(emptyList())
    val recommendation by viewModel.getSuggestionFlow().collectAsStateWithLifecycle(null)

    Box(
        modifier = Modifier
            .clipToBounds()
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        val listModifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopStart)
            .then(
                if (nestedScrollConnection != null)
                    Modifier.nestedScroll(nestedScrollConnection)
                else Modifier
            )
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = listModifier,
            state = listState,
            contentPadding = PaddingValues(
                start = padding.calculateStartPadding(layoutDirection) + 8.dp,
                top = padding.calculateTopPadding() + 8.dp,
                end = padding.calculateEndPadding(layoutDirection) + 8.dp,
                bottom = padding.calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            item(
                span = { GridItemSpan(maxCurrentLineSpan) }
            ) {
                Row {
                    ExploreButton(
                        text = stringResource(R.string.local_storage),
                        icon = Icons.Outlined.SdStorage,
                        modifier = Modifier.weight(1f),
                        onClick = { }
                    )

                    ExploreButton(
                        text = stringResource(R.string.bookmarks),
                        icon = Icons.Outlined.Bookmarks,
                        modifier = Modifier.weight(1f),
                        onClick = { }
                    )
                }
            }
            item(
                span = { GridItemSpan(maxCurrentLineSpan) }
            ) {
                Row {
                    ExploreButton(
                        text = stringResource(R.string.random),
                        icon = Icons.Outlined.Dice,
                        modifier = Modifier.weight(1f),
                        onClick = { },
                    )

                    ExploreButton(
                        text = stringResource(R.string.downloads),
                        icon = Icons.Outlined.Download,
                        modifier = Modifier.weight(1f),
                        onClick = { },
                    )
                }
            }
            item(
                span = { GridItemSpan(maxCurrentLineSpan) }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .clickable { recommendation?.id?.let { navigateToDetails(it) } }
                        .animateContentSize(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            text = "Рекомендации",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImageImpl(
                                coil = coil,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(72.dp))
                                    .aspectRatio(1f),
                                contentScale = ContentScale.Crop,
                                model = recommendation?.coverUrl,
                                contentDescription = ""
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                            ) {
                                Text(
                                    text = recommendation?.title ?: "",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 2
                                )
                                recommendation?.tags?.joinToString(", ") { it.title }?.let {
                                    Text(
                                        text = it,
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                        Button(
                            onClick = { navigateToSuggestions() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "More")
                        }
                    }
                }
            }
            items(
                items = sources.value,
                key = { it.id },
                contentType = { it }
            ) { item ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    SourceItem(
                        coil = coil,
                        faviconUrl = item.favicon,
                        title = item.title
                    ) {
                        navigateToSource(item)
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun RecommendationPreview() {
    TokushoTheme {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.manga_sources),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelLarge
            )
            TextButton(onClick = { /*TODO*/ }) {
                Text(text = stringResource(id = R.string.catalog))
            }
        }
    }
}