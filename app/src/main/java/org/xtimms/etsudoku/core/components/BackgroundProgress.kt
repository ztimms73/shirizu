package org.xtimms.etsudoku.core.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import org.xtimms.etsudoku.core.components.shape.WavyShape
import org.xtimms.etsudoku.utils.lang.clamp

@Composable
fun BackgroundProgress(
    color: Color,
    alpha: Float,
    percent: Float,
) {

    val animatedAlpha = animateFloatAsState(
        label = "alpha",
        targetValue = alpha,
        animationSpec = TweenSpec(500)
    ).value

    val percentAnimated = animateFloatAsState(
        label = "percentAnimated",
        targetValue = percent,
        animationSpec = TweenSpec(300),
    ).value

    val shift = remember { Animatable(0f) }

    Box(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .background(
                    color.copy(alpha = animatedAlpha),
                    shape = WavyShape(
                        period = 30.dp,
                        amplitude = percentAnimated.clamp(0.96f, 1f) * 2.dp,
                        shift = shift.value,
                    ),
                )
                .fillMaxHeight()
                .fillMaxWidth(percentAnimated),
        )
    }
}