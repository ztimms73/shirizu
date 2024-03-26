package org.xtimms.tokusho.core.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.xtimms.tokusho.ui.theme.TokushoTheme
import org.xtimms.tokusho.utils.material.combineColors
import org.xtimms.tokusho.utils.material.harmonize
import org.xtimms.tokusho.utils.material.toPalette

@Composable
fun RowScope.ReadButton() {

    val shift = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

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
    val percentWithNewSpentAnimated = animateFloatAsState(
        label = "percentWithNewSpentAnimated",
        targetValue = 0.3f,
        animationSpec = TweenSpec(300),
    ).value

    Card(
            modifier = Modifier
                .weight(1F)
                .height(54.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            onClick = {
                // appViewModel.openSheet(PathState(WALLET_SHEET))
            }
        ) {
            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.CenterEnd,
            ) {
                BackgroundProgress(MaterialTheme.colorScheme.primary)
                Row(
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Continue reading", color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(text = "Chap. 123 - Test", style = MaterialTheme.typography.labelMedium) // TODO
                    }
                    Spacer(modifier = Modifier.weight(1f))
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

@Preview(name = "The budget is almost completely spent")
@Composable
private fun Preview() {
    TokushoTheme {
        Row {
            ReadButton()
        }
    }
}

@Preview(name = "Budget half spent")
@Composable
private fun PreviewHalf() {
    TokushoTheme {
        Row {
            ReadButton()
        }
    }
}

@Preview(name = "Almost no budget")
@Composable
private fun PreviewFull() {
    TokushoTheme {
        Row {
            ReadButton()
        }
    }
}

@Preview(name = "Overspending budget")
@Composable
private fun PreviewOverspending() {
    TokushoTheme {
        Row {
            ReadButton()
        }
    }
}

@Preview(name = "Night mode", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewNightMode() {
    TokushoTheme {
        Row {
            ReadButton()
        }
    }
}