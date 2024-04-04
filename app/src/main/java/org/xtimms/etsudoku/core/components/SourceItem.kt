package org.xtimms.etsudoku.core.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.ImageLoader
import org.xtimms.etsudoku.core.AsyncImageImpl
import org.xtimms.etsudoku.ui.theme.EtsudokuTheme

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
            .width(88.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Card(
            modifier = Modifier
                .size(88.dp)
                .clip(MaterialTheme.shapes.large)
                .aspectRatio(1f)
        ) {
            AsyncImageImpl(
                coil = coil,
                model = faviconUrl,
                contentDescription = "favicon",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.large)
            )
        }
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

@Preview
@Composable
fun SourceItemPreview() {
    EtsudokuTheme {
        SourceItem(
            coil = ImageLoader(LocalContext.current),
            faviconUrl = "".toUri(),
            title = "Test",
            onClick = { }
        )
    }
}