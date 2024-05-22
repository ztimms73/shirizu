package org.xtimms.shirizu.core.components

import android.content.res.Resources
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import org.xtimms.shirizu.R
import org.xtimms.shirizu.sections.library.history.SortOption

@Composable
internal fun ColumnScope.SortDropdownMenuContent(
    sortOptions: List<SortOption>,
    onItemClick: (SortOption) -> Unit,
    modifier: Modifier = Modifier,
    currentSortOption: SortOption? = null,
) {
    val resources = LocalContext.current.resources
    for (sort in sortOptions) {
        DropdownMenuItem(
            text = {
                Text(
                    text = sort.label(resources),
                    fontWeight = if (sort == currentSortOption) FontWeight.Bold else null,
                )
            },
            onClick = { onItemClick(sort) },
            modifier = modifier,
        )
    }
}

internal fun SortOption.label(resources: Resources): String = when (this) {
    SortOption.ALPHABETICAL -> resources.getString(R.string.sort_alphabetically)
    SortOption.DATE_ADDED -> resources.getString(R.string.sort_date_added)
}