package org.xtimms.tokusho.sections.stats

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koitharu.kotatsu.parsers.model.MangaChapter
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.shiki.ui.theme.colorMax
import org.xtimms.shiki.ui.theme.colorMin
import org.xtimms.tokusho.R
import org.xtimms.tokusho.utils.material.combineColors
import org.xtimms.tokusho.utils.material.harmonize
import org.xtimms.tokusho.utils.material.toPalette
import java.math.BigDecimal

@Composable
fun MinMaxReadCard(
    modifier: Modifier = Modifier,
    isMin: Boolean,
) {

    val context = LocalContext.current

    val minValue = 5
    val maxValue = 8
    val currValue = 7

    val harmonizedColor = toPalette(
        harmonize(
            combineColors(
                colorMin,
                colorMax,
                if ((maxValue - minValue) == 0) {
                    if (isMin) 0f else 1f
                } else if (maxValue != 0) {
                    ((currValue - minValue) / (maxValue - minValue)).toFloat()
                } else {
                    0f
                },
            )
        )
    )

    StatsCard(
        modifier = modifier,
        value = "313",
        label = stringResource(if (isMin) R.string.min_chapters_read else R.string.max_chapters_read),
        valueFontSize = MaterialTheme.typography.titleLarge.fontSize,
        colors = CardDefaults.cardColors(
            containerColor = harmonizedColor.container,
            contentColor = harmonizedColor.onContainer,
        ),
        content = {
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "-",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = MaterialTheme.typography.bodyLarge.fontWeight
            )

            Row(
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(16.dp),
                    imageVector = Icons.AutoMirrored.Outlined.Label,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Test",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight
                )
            }
        },
        backdropContent = {
            ChaptersChart(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                chapters = listOf(
                    MangaChapter(id = 1, name = "", number = 1, "", "", 0L, "", MangaSource.DUMMY),
                    MangaChapter(id = 1, name = "", number = 1, "", "", 0L, "", MangaSource.DUMMY),
                    MangaChapter(id = 1, name = "", number = 1, "", "", 0L, "", MangaSource.DUMMY),
                    MangaChapter(id = 1, name = "", number = 1, "", "", 0L, "", MangaSource.DUMMY),
                    MangaChapter(id = 1, name = "", number = 1, "", "", 0L, "", MangaSource.DUMMY),
                    MangaChapter(id = 1, name = "", number = 1, "", "", 0L, "", MangaSource.DUMMY)
                )
            )
        }
    )
}