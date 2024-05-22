package org.xtimms.shirizu.sections.explore.sources

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.shirizu.core.ShirizuAsyncImage
import org.xtimms.shirizu.core.parser.favicon.faviconUri

private val defaultModifier = Modifier
    .height(42.dp)
    .aspectRatio(1f)

@Composable
fun SourceIcon(
    source: MangaSource,
    modifier: Modifier = Modifier,
) {
    val icon = source.faviconUri()

    Card(
        modifier = modifier.then(defaultModifier),
    ) {
        ShirizuAsyncImage(
            model = icon,
            contentDescription = "favicon",
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.medium)
        )
    }
}