package org.xtimms.etsudoku.sections.stats.categories

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.xtimms.etsudoku.utils.material.combineColors
import org.xtimms.etsudoku.utils.material.harmonizeWithColor
import org.xtimms.etsudoku.utils.material.toPalette

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
    modifier: Modifier = Modifier
) {

    val isNightMode = isSystemInDarkTheme()

    val colors = baseColors.map {
        toPalette(
            color = harmonizeWithColor(
                designColor = it,
                sourceColor = MaterialTheme.colorScheme.primary
            ),
        )
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = combineColors(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.surfaceVariant,
                angle = 0.3f,
            ),
        )
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                DonutChart(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                        .size(64.dp),
                    items = emptyList(),
                )
                FlowRow(Modifier.padding(4.dp, 4.dp)) {

                }
            }
        }
    }

}