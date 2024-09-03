package org.xtimms.shirizu.sections.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.koitharu.kotatsu.parsers.model.MangaChapter
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.ShirizuAsyncImage
import org.xtimms.shirizu.core.components.PreferenceItem
import org.xtimms.shirizu.core.components.PreferenceSubtitle
import org.xtimms.shirizu.sections.shelf.ShelfCategory
import org.xtimms.shirizu.sections.stats.ChaptersChart
import org.xtimms.shirizu.sections.stats.TimeCard
import org.xtimms.shirizu.sections.stats.categories.CategoriesChart
import org.xtimms.shirizu.ui.theme.ShirizuTheme
import org.xtimms.shirizu.utils.composable.bodyWidth

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.bodyWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            AsyncImage(
                model = "https://avatars.githubusercontent.com/u/61558546?v=4",
                contentDescription = "profile",
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(100))
                    .size(100.dp)
            )
        }
        item {
            Text(text = "Xtimms", style = MaterialTheme.typography.titleLarge)
        }
        item {
            Text(text = "My status", style = MaterialTheme.typography.bodyMedium)
        }
        item {
            HorizontalDivider(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp))
        }
        item {
            PreferenceSubtitle(text = stringResource(id = R.string.statistics))
        }
        item {
            TimeCard(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .padding(horizontal = 16.dp)
            )
        }
        item {
            CategoriesChart(
                modifier = Modifier.padding(16.dp),
                categories = listOf(
                    ShelfCategory(1, "Test 1", 3),
                    ShelfCategory(2, "Test 2", 4),
                    ShelfCategory(3, "Test 3", 6),
                    ShelfCategory(4, "Test 4", 7),
                    ShelfCategory(5, "Test 5", 13),
                    ShelfCategory(6, "Test 6", 12),
                )
            )
        }
        item {
            PreferenceSubtitle(text = stringResource(id = R.string.menu))
        }
        item {
            PreferenceItem(
                icon = Icons.Outlined.Settings,
                title = stringResource(id = R.string.settings)
            )
        }
        item {
            PreferenceItem(
                icon = Icons.AutoMirrored.Outlined.HelpOutline,
                title = stringResource(id = R.string.help_centre)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    ShirizuTheme {
        ProfileScreen()
    }
}