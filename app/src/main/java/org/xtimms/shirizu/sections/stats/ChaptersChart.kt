package org.xtimms.shirizu.sections.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.koitharu.kotatsu.parsers.model.MangaChapter
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.shirizu.ui.theme.colorMax
import org.xtimms.shirizu.ui.theme.colorMin
import org.xtimms.shirizu.ui.theme.ShirizuTheme
import org.xtimms.shirizu.utils.material.combineColors
import org.xtimms.shirizu.utils.material.harmonize
import org.xtimms.shirizu.utils.material.toPalette
import kotlin.math.abs

@Composable
fun ChaptersChart(
    modifier: Modifier = Modifier,
    chapters: List<MangaChapter>,
    chartPadding: PaddingValues = PaddingValues(0.dp),
) {

    val harmonizeColorMax = if (isSystemInDarkTheme()) {
        toPalette(harmonize(colorMax)).onContainer
    } else {
        toPalette(harmonize(colorMax)).main
    }
    val harmonizeColorMin = if (isSystemInDarkTheme()) {
        toPalette(harmonize(colorMin)).onContainer
    } else {
        toPalette(harmonize(colorMin)).main
    }

    val colors = listOf(
        harmonizeColorMax,
        harmonize(designColor = combineColors(harmonizeColorMax, harmonizeColorMin, 0.5f)),
        harmonizeColorMin
    )

    val minSpentValue = 6
    val maxSpentValue = 17
    val range = maxSpentValue - minSpentValue

    val localDensity = LocalDensity.current

    val layoutDirection = when (LocalConfiguration.current.layoutDirection) {
        0 -> LayoutDirection.Rtl
        1 -> LayoutDirection.Ltr
        else -> LayoutDirection.Rtl
    }

    val topOffset = with(localDensity) { chartPadding.calculateTopPadding().toPx() }
    val bottomOffset = with(localDensity) { chartPadding.calculateBottomPadding().toPx() }
    val startOffset =
        with(localDensity) { chartPadding.calculateStartPadding(layoutDirection).toPx() }
    val endOffset = with(localDensity) { chartPadding.calculateEndPadding(layoutDirection).toPx() }

    val (indexMarked, firstShowIndex, lastShowIndex) = Triple(1, 0, 5)

    Canvas(modifier = modifier) {
        val width = this.size.width
        val height = this.size.height
        val heightWithPaddings = height - topOffset - bottomOffset
        val widthWithPaddings = width - startOffset - endOffset
        val size = 5.toFloat().coerceAtLeast(1f)

        val trianglePath = Path().let {
            var lastY = 0f

            chapters.subList(firstShowIndex, lastShowIndex).forEachIndexed { index, read ->
                val scale = if (range == 0) {
                    0.5f
                } else {
                    5.minus(minSpentValue).div(2).toFloat()
                }

                if (index == 0) {
                    lastY = topOffset + heightWithPaddings * (1 - scale)
                    it.moveTo(
                        0f,
                        lastY
                    )
                }

                it.cubicTo(
                    startOffset + widthWithPaddings * ((index - 0.5f).coerceAtLeast(0f) / size),
                    lastY,
                    startOffset + widthWithPaddings * ((index - 0.5f).coerceAtLeast(0f) / size),
                    topOffset + heightWithPaddings * (1 - scale),
                    startOffset + widthWithPaddings * (index / size),
                    topOffset + heightWithPaddings * (1 - scale),
                )

                lastY = topOffset + heightWithPaddings * (1 - scale)
            }

            it.lineTo(width, lastY)

            it.lineTo(width, height)
            it.lineTo(0f, height)

            it
        }

        val chartColors = colors.mapIndexed { index, color ->
            color.copy(alpha = 0.3f - abs(0.5f - (index / (colors.size - 1))) * 0.25f)
        }

        drawPath(
            path = trianglePath,
            Brush.verticalGradient(colors = chartColors),
            style = Fill
        )

        val scale = 0.5f

        val color = combineColors(
            harmonizeColorMin,
            harmonizeColorMax,
            scale,
        )

        val x = startOffset + widthWithPaddings * ((indexMarked - firstShowIndex) / size)
        val y = topOffset + heightWithPaddings * (1 - scale)

        drawCircle(
            color = color.copy(0.2f),
            radius = with(localDensity) { 8.dp.toPx() },
            center = Offset(x, y)
        )

        drawCircle(
            color = color,
            radius = with(localDensity) { 3.dp.toPx() },
            center = Offset(x, y)
        )
    }
}

@Preview
@Composable
private fun PreviewChart() {
    ShirizuTheme {
        ChaptersChart(
            modifier = Modifier.size(100.dp),
            chapters = listOf(
                MangaChapter(id = 1, name = "", number = 1, "", "", 0L, "", MangaSource.DUMMY),
                MangaChapter(id = 1, name = "", number = 1, "", "", 0L, "", MangaSource.DUMMY),
                MangaChapter(id = 1, name = "", number = 1, "", "", 0L, "", MangaSource.DUMMY),
                MangaChapter(id = 1, name = "", number = 1, "", "", 0L, "", MangaSource.DUMMY),
                MangaChapter(id = 1, name = "", number = 1, "", "", 0L, "", MangaSource.DUMMY),
                MangaChapter(id = 1, name = "", number = 1, "", "", 0L, "", MangaSource.DUMMY)
            ),
            chartPadding = PaddingValues(vertical = 16.dp)
        )
    }
}