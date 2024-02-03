package org.xtimms.tokusho.core

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import org.xtimms.tokusho.R

@Composable
fun AsyncImageImpl(
    coil: ImageLoader,
    model: Any? = null,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    transform: (AsyncImagePainter.State) -> AsyncImagePainter.State = AsyncImagePainter.DefaultTransform,
    onState: ((AsyncImagePainter.State) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Crop,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    isPreview: Boolean = false,
) {
    if (isPreview) Image(
        painter = painterResource(R.drawable.sample),
        contentDescription = contentDescription,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        colorFilter = colorFilter,
    )
    else AsyncImage(
        imageLoader = coil,
        model = model?.takeUnless { it == "" },
        contentDescription = contentDescription,
        modifier = modifier,
        transform = transform,
        onState = onState,
        alignment = alignment,
        contentScale = contentScale,
        colorFilter = colorFilter,
        filterQuality = filterQuality
    )
}