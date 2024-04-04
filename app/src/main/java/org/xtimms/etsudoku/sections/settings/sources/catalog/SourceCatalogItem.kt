package org.xtimms.etsudoku.sections.settings.sources.catalog

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.etsudoku.core.AsyncImageImpl
import org.xtimms.etsudoku.core.parser.favicon.faviconUri

@Composable
fun SourceCatalogItem(
    coil: ImageLoader,
    source: MangaSource,
    modifier: Modifier = Modifier,
) {

    Card(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImageImpl(
                coil = coil,
                contentDescription = null,
                model = source.faviconUri()
            )
            Text(
                text = source.title,
                modifier = Modifier
                    .padding(start = 16.dp),
            )
        }
    }
}