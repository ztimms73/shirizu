package org.xtimms.tokusho.sections.history

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.collapsable
import org.xtimms.tokusho.core.components.ListGroupHeader
import org.xtimms.tokusho.core.components.effects.RowEntity
import org.xtimms.tokusho.core.components.effects.RowEntityType
import org.xtimms.tokusho.core.components.effects.animatedItemsIndexed
import org.xtimms.tokusho.core.components.effects.updateAnimatedItemsState
import org.xtimms.tokusho.core.prefs.AppSettings
import org.xtimms.tokusho.core.prefs.SWIPE_TUTORIAL
import org.xtimms.tokusho.core.screens.EmptyScreen
import org.xtimms.tokusho.core.screens.LoadingScreen
import org.xtimms.tokusho.utils.lang.calculateTimeAgo
import org.xtimms.tokusho.utils.lang.isSameDay
import java.time.Instant
import kotlin.math.abs
import kotlin.math.absoluteValue

const val HISTORY_DESTINATION = "history"

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HistoryView(
    coil: ImageLoader,
    viewModel: HistoryViewModel = hiltViewModel(),
    topBarHeightPx: Float,
    topBarOffsetY: Animatable<Float, AnimationVector1D> = Animatable(0f),
    padding: PaddingValues,
    navigateToDetails: (Long) -> Unit,
    navigateToReader: () -> Unit
) {
    val scrollState = rememberScrollState()
    var isUserTrySwipe by remember { mutableStateOf(false) }

    val history by viewModel.content.collectAsStateWithLifecycle(null)

    DisposableEffect(Unit) {
        onDispose {
            if (history?.isNotEmpty() == true && isUserTrySwipe) {
                AppSettings.updateValue(SWIPE_TUTORIAL, isUserTrySwipe)
            }
        }
    }

    val animatedList = run {
        val list = emptyList<RowEntity>().toMutableList()
        var readDate: Instant? = null
        history?.forEach { item ->

            if (readDate === null || !isSameDay(
                    item.history.updatedAt.toEpochMilli(),
                    readDate!!.toEpochMilli()
                )
            ) {
                readDate = item.history.updatedAt

                list.add(
                    RowEntity(
                        type = RowEntityType.Header,
                        key = "header-${readDate}",
                        itemModel = null,
                        day = readDate!!,
                    )
                )
            }
            list.add(
                RowEntity(
                    type = RowEntityType.Item,
                    key = "item-${item.manga.id}",
                    day = readDate!!,
                    itemModel = item
                )
            )
        }
        updateAnimatedItemsState(newList = list.toList().map { it })
    }

    Box(
        modifier = Modifier
            .clipToBounds()
            .fillMaxSize(),
    ) {
        history.let {
            if (it == null) {
                LoadingScreen(Modifier.padding(padding))
            } else if (it.isEmpty()) {
                EmptyScreen(
                    icon = Icons.Outlined.History,
                    title = R.string.empty_history_title,
                    description = R.string.empty_history_description
                )
            } else {
                Column(Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .collapsable(
                                state = scrollState,
                                topBarHeightPx = topBarHeightPx,
                                topBarOffsetY = topBarOffsetY
                            )
                            .padding(padding)
                    ) {
                        animatedItemsIndexed(
                            state = animatedList.value,
                            key = { rowItem -> rowItem.key },
                        ) { _, item ->
                            when (item.type) {
                                RowEntityType.Header -> ListGroupHeader(
                                    calculateTimeAgo(item.day).format(
                                        LocalContext.current.resources
                                    )
                                )

                                RowEntityType.Item -> SwipeActions(
                                    startActionsConfig = SwipeActionsConfig(
                                        threshold = 0.33f,
                                        background = MaterialTheme.colorScheme.errorContainer,
                                        backgroundActive = MaterialTheme.colorScheme.error,
                                        iconTint = MaterialTheme.colorScheme.onError,
                                        icon = Icons.Outlined.DeleteForever,
                                        stayDismissed = true,
                                        onDismiss = {
                                            viewModel.removeFromHistory(item.itemModel!! as HistoryItemModel)
                                        }
                                    ),
                                    endActionsConfig = SwipeActionsConfig(
                                        threshold = 0.33f,
                                        background = MaterialTheme.colorScheme.tertiaryContainer,
                                        backgroundActive = MaterialTheme.colorScheme.tertiary,
                                        iconTint = MaterialTheme.colorScheme.onTertiary,
                                        icon = Icons.Outlined.PlayArrow,
                                        stayDismissed = false,
                                        onDismiss = {
                                            navigateToReader()
                                        }
                                    ),
                                    onTried = { isUserTrySwipe = true },
                                    showTutorial = false,
                                ) { state ->
                                    val size = with(LocalDensity.current) {
                                        java.lang.Float.max(
                                            java.lang.Float.min(
                                                16.dp.toPx(),
                                                abs(state.offset.value)
                                            ), 0f
                                        ).toDp()
                                    }

                                    val animateCorners by remember {
                                        derivedStateOf {
                                            state.offset.value.absoluteValue > 30
                                        }
                                    }
                                    val startCorners by animateDpAsState(
                                        targetValue = when {
                                            state.dismissDirection == DismissDirection.StartToEnd &&
                                                    animateCorners -> 8.dp

                                            else -> 0.dp
                                        }, label = "startCorners"
                                    )
                                    val endCorners by animateDpAsState(
                                        targetValue = when {
                                            state.dismissDirection == DismissDirection.EndToStart &&
                                                    animateCorners -> 8.dp

                                            else -> 0.dp
                                        }, label = "endCorners"
                                    )

                                    Box(
                                        modifier = Modifier.height(IntrinsicSize.Min)
                                    ) {
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(
                                                    vertical = min(
                                                        size / 4f,
                                                        4.dp
                                                    )
                                                )
                                                .clip(RoundedCornerShape(size)),
                                            color = MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(
                                                topStart = startCorners,
                                                bottomStart = startCorners,
                                                topEnd = endCorners,
                                                bottomEnd = endCorners,
                                            ),
                                        ) {
                                            // nothing
                                        }
                                        Box(
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            HistoryItem(
                                                coil = coil,
                                                history = (item.itemModel!! as HistoryItemModel),
                                                onClick = { navigateToDetails((item.itemModel!! as HistoryItemModel).manga.id) },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}