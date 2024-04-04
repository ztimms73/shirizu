package org.xtimms.etsudoku.core.components.effects

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.random.Random

data class Snowflake(
    var x: Float,
    var y: Float,
    var radius: Float,
    var speed: Float
)

@Composable
fun SnowfallEffect() {
    val snowflakes = remember { List(100) { generateRandomSnowflake() } }
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing)
        ), label = ""
    )

    Canvas(modifier = Modifier.fillMaxSize().background(Color.Transparent)) {
        snowflakes.forEach { snowflake ->
            drawSnowflake(snowflake, offsetY % size.height)
        }
    }
}

fun generateRandomSnowflake(): Snowflake {
    return Snowflake(
        x = Random.nextFloat(),
        y = Random.nextFloat() * 1000f,
        radius = Random.nextFloat() * 2f + 2f, // Snowflake size
        speed = Random.nextFloat() * 0.5f + 1f  // Falling speed
    )
}

fun DrawScope.drawSnowflake(snowflake: Snowflake, offsetY: Float) {
    val newY = (snowflake.y + offsetY * snowflake.speed) % size.height
    drawCircle(Color.White, radius = snowflake.radius, center = Offset(snowflake.x * size.width, newY))
}