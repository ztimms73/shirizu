package org.xtimms.shirizu.core.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.model.MangaHistory
import org.xtimms.shirizu.sections.details.model.HistoryInfo
import org.xtimms.shirizu.ui.theme.ShirizuTheme
import java.time.Instant

@Composable
fun RowScope.ReadButton(
    info: HistoryInfo,
    estimatedReadTime: String
) {

    val shift = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    val animatedCardContainerColor = animateColorAsState(
        label = "animatedCardContainerColor",
        targetValue = if (info.totalChapters == 0) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
        animationSpec = TweenSpec(500)
    ).value

    val animatedCardContentColor = animateColorAsState(
        label = "animatedCardContentColor",
        targetValue = if (info.totalChapters == 0) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
        animationSpec = TweenSpec(500)
    ).value

    LaunchedEffect(Unit) {
        fun anim() {
            coroutineScope.launch {
                shift.animateTo(
                    1f,
                    animationSpec = FloatTweenSpec(4000, 0, LinearEasing)
                )
                shift.snapTo(0f)
                anim()
            }
        }
        anim()
    }

    Card(
        modifier = Modifier
            .weight(1F)
            .height(54.dp),
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = animatedCardContainerColor,
            contentColor = animatedCardContentColor,
        ),
        onClick = {
            // todo
        }
    ) {
        Box(
            modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            BackgroundProgress(
                if (info.totalChapters == 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                if (!info.isValid) 0.1f else 0.33f,
                info.history?.percent?.coerceIn(0f, 1f) ?: 0f
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithLayer {
                        drawContent()
                        val leftOffset = size.width - 20.dp.toPx()
                        drawRect(
                            topLeft = Offset(leftOffset, 0f),
                            size = Size(
                                20.dp.toPx(),
                                size.height,
                            ),
                            blendMode = BlendMode.SrcIn,
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Black,
                                    Color.Black.copy(alpha = 0f),
                                ),
                                startX = leftOffset,
                                endX = leftOffset + 14.dp.toPx()
                            )
                        )
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                val infiniteTransition by rememberInfiniteTransition("infiniteTransition").animateValue(
                    label = "infiniteTransition",
                    initialValue = 0,
                    targetValue = 2,
                    typeConverter = Int.VectorConverter,
                    animationSpec = infiniteRepeatable(tween(15000), RepeatMode.Restart)
                )
                val chaptersSubtitle = when {
                    !info.isValid -> stringResource(R.string.loading_)
                    info.currentChapter >= 0 -> when (infiniteTransition) {
                        1 -> stringResource(
                            R.string.chapter_d_of_d,
                            info.currentChapter + 1,
                            info.totalChapters,
                        )
                        0 -> estimatedReadTime
                        else -> estimatedReadTime
                    }

                    info.totalChapters == 0 -> stringResource(R.string.no_chapters)
                    else -> LocalContext.current.resources.getQuantityString(
                        R.plurals.chapters,
                        info.totalChapters,
                        info.totalChapters,
                    )
                }
                Text(
                    text = if (info.history != null) {
                        stringResource(R.string.continue_reading)
                    } else {
                        stringResource(R.string.read)
                    },
                )
                AnimatedContent(targetState = chaptersSubtitle, label = "Subtitle animation") {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}

fun ContentDrawScope.drawWithLayer(block: ContentDrawScope.() -> Unit) {
    with(drawContext.canvas.nativeCanvas) {
        val checkPoint = saveLayer(null, null)
        block()
        restoreToCount(checkPoint)
    }
}

fun Modifier.drawWithLayer(block: ContentDrawScope.() -> Unit) = this.then(
    Modifier.drawWithContent {
        drawWithLayer {
            block()
        }
    }
)

@Preview
@Composable
private fun Preview() {
    ShirizuTheme {
        Row {
            ReadButton(
                HistoryInfo(
                    100,
                    33,
                    MangaHistory(
                        Instant.now(),
                        Instant.now(),
                        1,
                        3,
                        1,
                        50f
                    )
                ),
                "123"
            )
        }
    }
}

@Preview(name = "Night mode", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewNightMode() {
    ShirizuTheme {
        Row {
            ReadButton(
                HistoryInfo(
                    100,
                    33,
                    MangaHistory(
                        Instant.now(),
                        Instant.now(),
                        1,
                        3,
                        1,
                        50f
                    )
                ),
                "123"
            )
        }
    }
}