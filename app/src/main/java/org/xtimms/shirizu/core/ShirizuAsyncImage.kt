package org.xtimms.shirizu.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import org.xtimms.shirizu.LocalImageLoader
import org.xtimms.shirizu.R
import org.xtimms.shirizu.utils.composable.rememberResourceBitmapPainter

@Composable
fun ShirizuAsyncImage(
    model: Any? = null,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    AsyncImage(
        imageLoader = LocalImageLoader.current,
        model = model,
        placeholder = ColorPainter(Color(0x1F888888)),
        error = rememberResourceBitmapPainter(id = R.drawable.cover_error),
        fallback = rememberResourceBitmapPainter(id = R.drawable.cover_loading),
        modifier = modifier,
        contentScale = contentScale,
        contentDescription = contentDescription
    )
}