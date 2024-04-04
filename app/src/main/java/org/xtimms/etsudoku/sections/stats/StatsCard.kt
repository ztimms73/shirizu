package org.xtimms.etsudoku.sections.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import org.xtimms.etsudoku.ui.theme.EtsudokuTheme

@Composable
fun TimeCard(
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(),
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp, horizontal = 24.dp)
) {
    val context = LocalContext.current

    StatsCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        label = "Test",
        value = "330 days",
        valueFontSize = MaterialTheme.typography.displaySmall.fontSize,
        colors = colors,
        content = {
            Spacer(modifier = Modifier.height(16.dp))
            Layout(
                modifier = Modifier.height(IntrinsicSize.Min),
                measurePolicy = growByMiddleChildRowMeasurePolicy(LocalDensity.current),
                content = {
                    Column {
                        Text(
                            text = "01.01.1970",
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                        )
                    }

                    Box(
                        modifier = Modifier
                    ) {
                        Arrow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .fillMaxHeight()
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "01.01.1970",
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight
                        )
                    }
                }
            )
        }
    )
}

@Composable
fun StatsCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp, horizontal = 24.dp),
    colors: CardColors = CardDefaults.cardColors(),
    valueFontSize: TextUnit = MaterialTheme.typography.titleLarge.fontSize,
    content: @Composable ColumnScope.() -> Unit = { },
    backdropContent: @Composable () -> Unit = { },
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        colors = colors
    ) {
        val textColor = LocalContentColor.current

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                backdropContent()
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.displayLarge,
                    fontSize = valueFontSize,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false,
                    lineHeight = TextUnit(0.2f, TextUnitType.Em)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor.copy(alpha = 0.6f),
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false,
                )
                Spacer(modifier = Modifier.height(4.dp))

                CompositionLocalProvider(
                    LocalContentColor provides textColor
                ) {
                    Column(
                        content = content
                    )
                }
            }
        }
    }
}

@Composable
fun Arrow(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    Canvas(modifier = modifier) {
        val width = this.size.width
        val height = this.size.height
        val heightHalf = height / 2

        val thickness = 6
        val thicknessHalf = thickness / 2

        val trianglePath = Path().let {
            it.moveTo(11f, heightHalf - thicknessHalf)
            it.lineTo(width - 22.4f, heightHalf - thicknessHalf)
            it.lineTo(width - 37.4f, heightHalf - 18)
            it.lineTo(width - 33, heightHalf - 22.4f)
            it.lineTo(width - 10.5f, heightHalf)
            it.lineTo(width - 33, heightHalf + 22.4f)
            it.lineTo(width - 37.4f, heightHalf + 18)
            it.lineTo(width - 22.4f, heightHalf + thicknessHalf)
            it.lineTo(width - 22.4f, heightHalf + thicknessHalf)
            it.lineTo(11f, heightHalf + thicknessHalf)

            it.close()

            it
        }

        drawPath(
            path = trianglePath,
            SolidColor(tint),
            style = Fill
        )
    }
}

fun growByMiddleChildRowMeasurePolicy(localDensity: Density) =
    MeasurePolicy { measurables, constraints ->
        val minMiddleWidth = with(localDensity) { (24 + 32).dp.toPx().toInt() }

        val first = measurables[0]
            .measure(
                constraints.copy(
                    maxWidth = (constraints.maxWidth - minMiddleWidth) / 2
                )
            )
        val last = measurables[2]
            .measure(
                constraints.copy(
                    maxWidth = (constraints.maxWidth - minMiddleWidth) / 2
                )
            )

        val height = listOf(first, last).minOf { it.height }

        layout(constraints.maxWidth, height) {
            first.placeRelative(0, 0, 0f)

            val middleWidth =
                (constraints.maxWidth - first.width - last.width).coerceAtLeast(minMiddleWidth)

            val middle = measurables[1]
                .measure(
                    constraints.copy(
                        maxWidth = middleWidth,
                        minWidth = middleWidth,
                    )
                )

            middle.placeRelative(first.width, 0, 0f)

            last.placeRelative(constraints.maxWidth - last.width, 0, 0f)
        }
    }

@Preview
@Composable
private fun TimeCardPreview() {
    EtsudokuTheme {
        TimeCard(
            modifier = Modifier.height(IntrinsicSize.Min),
        )
    }
}

@Preview
@Composable
private fun StatsCardPreview() {
    EtsudokuTheme {
        StatsCard(
            value = "value",
            label = "label"
        )
    }
}