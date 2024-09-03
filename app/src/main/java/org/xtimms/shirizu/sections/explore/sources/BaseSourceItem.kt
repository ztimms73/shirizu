package org.xtimms.shirizu.sections.explore.sources

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import org.koitharu.kotatsu.parsers.model.ContentType
import org.koitharu.kotatsu.parsers.model.MangaParserSource
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.shirizu.R
import org.xtimms.shirizu.utils.LocaleHelper
import org.xtimms.shirizu.utils.composable.secondaryItemAlpha
import java.util.Locale

@Composable
fun BaseSourceItem(
    source: MangaParserSource,
    modifier: Modifier = Modifier,
    showTypeInContent: Boolean = true,
    onClickItem: () -> Unit = {},
    onLongClickItem: () -> Unit = {},
    icon: @Composable RowScope.(MangaSource) -> Unit = defaultIcon,
    action: @Composable RowScope.(MangaSource) -> Unit = {},
    content: @Composable RowScope.(MangaParserSource, String?) -> Unit = defaultContent,
) {
    fun getPrettyContentTypeName(type: ContentType?, context: Context): String {
        if (type == null) {
            return ""
        }
        return when (type) {
            ContentType.COMICS -> context.resources.getString(R.string.comics)
            ContentType.HENTAI -> context.resources.getString(R.string.hentai)
            ContentType.MANGA -> context.resources.getString(R.string.manga)
            ContentType.OTHER -> context.resources.getString(R.string.other)
        }
    }

    val sourceTypeString = getPrettyContentTypeName(source.contentType, LocalContext.current).takeIf {
        showTypeInContent
    }

    BaseExploreItem(
        modifier = modifier,
        onClickItem = onClickItem,
        onLongClickItem = onLongClickItem,
        icon = { icon.invoke(this, source) },
        action = { action.invoke(this, source) },
        content = { content.invoke(this, source, sourceTypeString) },
    )
}

private val defaultIcon: @Composable RowScope.(MangaSource) -> Unit = { source ->
    SourceIcon(source = source)
}

private val defaultContent: @Composable RowScope.(MangaParserSource, String?) -> Unit = { source, sourceLangString ->
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .weight(1f),
    ) {
        Text(
            text = source.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
        )
        if (sourceLangString != null) {
            Text(
                modifier = Modifier.secondaryItemAlpha(),
                text = sourceLangString,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}