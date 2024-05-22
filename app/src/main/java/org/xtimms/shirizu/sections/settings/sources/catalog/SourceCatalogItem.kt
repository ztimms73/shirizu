package org.xtimms.shirizu.sections.settings.sources.catalog

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.shirizu.core.ShirizuAsyncImage
import org.xtimms.shirizu.core.parser.favicon.faviconUri
import org.xtimms.shirizu.ui.theme.ShirizuTheme

@Composable
fun SourceCatalogItem(
    source: MangaSource,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ShirizuAsyncImage(
            modifier = Modifier.size(42.dp),
            contentDescription = null,
            model = source.faviconUri()
        )
        Text(
            text = source.title,
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f),
        )
        IconButton(onClick = { /*TODO*/ }) {
            Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
        }
    }
}

@Preview
@Composable
fun SourceCatalogItemPreview() {
    ShirizuTheme {
        SourceCatalogItem(source = MangaSource.MANGADEX)
    }
}