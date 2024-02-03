package org.xtimms.tokusho.core.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import org.xtimms.tokusho.core.AsyncImageImpl

@Composable
fun SourceItem(
    coil: ImageLoader,
    faviconUrl: Uri,
    title: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .width(96.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        AsyncImageImpl(
            coil = coil,
            model = faviconUrl,
            contentDescription = "favicon",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(96.dp)
                .clip(RoundedCornerShape(8.dp))
                .aspectRatio(1f)
        )
        Text(
            text = title,
            modifier = Modifier
                .padding(top = 4.dp, bottom = 4.dp)
                .align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = maxLines
        )
    }
}