package org.xtimms.shirizu.sections.stats.categories

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.xtimms.shirizu.R
import org.xtimms.shirizu.sections.shelf.ShelfCategory
import org.xtimms.shirizu.utils.material.HarmonizedColorPalette
import org.xtimms.shirizu.utils.material.combineColors
import org.xtimms.shirizu.utils.material.harmonize
import org.xtimms.shirizu.utils.material.harmonizeWithColor
import org.xtimms.shirizu.utils.material.toPalette
import java.math.BigDecimal

data class TagUsage(
    val name: String,
    val mangaCount: BigDecimal,
    var color: HarmonizedColorPalette? = null
)

var baseColors = listOf(
    Color(0xFFF86BAE),
    Color(0xFFF36FFF),
    Color(0xFFAB96FF),
    Color(0xFF5FC7E7),
    Color(0xFF75E584),
    Color(0xFFFFD386),
    Color(0xFFEF7564),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoriesChart(
    modifier: Modifier = Modifier,
    categories: List<ShelfCategory>
) {

    val isNightMode = isSystemInDarkTheme()
    val labelWithoutTag = stringResource(R.string.progress)
    val maxDisplay = 7

    val colors = baseColors.map {
        toPalette(
            color = harmonizeWithColor(
                designColor = it,
                sourceColor = MaterialTheme.colorScheme.primary
            ),
        )
    }
    val restColor = toPalette(
        color = harmonize(
            designColor = Color(0xFF222222),
            sourceColor = MaterialTheme.colorScheme.primary
        ),
    ).copy(
        main = if (isNightMode) Color(0xFFF0F0F0) else Color(0xFF222222),
        onSurface = if (isNightMode) Color(0xFF1A1A1A) else Color(0xFFF4F4F4)
    )
    val stubColor = toPalette(
        color = harmonize(
            designColor = Color(0xFFCCCCCC),
            sourceColor = MaterialTheme.colorScheme.primary
        ),
    ).copy(
        main = if (isNightMode) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFCCCCCC),
    )

    var offsetColor = 0

    val tags by remember {
        var result = categories
            .groupBy { it.title.trim() }
            .map { tag ->
                TagUsage(
                    tag.key,
                    tag.value.map { it.mangaCount.toBigDecimal() }.reduce { acc, next -> acc + next },
                )
            }
            .sortedBy { it.name }
            .reversed()
            .toList()

        // Set colors
        result.subList(0, result.size.coerceAtMost(maxDisplay)).forEachIndexed { index, tagUsage ->
            tagUsage.color = colors.getOrNull(index - offsetColor) ?: colors.last()
        }

        // Combine rest tags to one
        if (result.size > maxDisplay) {
            result = result.slice(0..<maxDisplay) + TagUsage(
                name = labelWithoutTag,
                mangaCount = result
                    .slice(maxDisplay until result.size)
                    .map { it.mangaCount }
                    .reduce { acc, next -> acc + next },
                color = restColor,
            )
        }

        mutableStateOf(result)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = combineColors(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.surfaceVariant,
                angle = 0.3f,
            ),
        )
    ) {
        DonutChart(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                .size(64.dp),
            items = tags,
        )
        FlowRow(Modifier.padding(4.dp, 4.dp)) {
            tags.forEach { tag ->
                TagAmount(
                    modifier = Modifier.padding(4.dp, 4.dp),
                    value = tag.name,
                    palette = tag.color,
                    amount = tag.mangaCount
                )
            }
        }
    }
}