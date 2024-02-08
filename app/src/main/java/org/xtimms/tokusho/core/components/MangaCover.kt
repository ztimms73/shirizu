package org.xtimms.tokusho.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import coil.ImageLoader
import coil.compose.AsyncImage
import org.xtimms.tokusho.core.AsyncImageImpl

enum class MangaCover(val ratio: Float) {
    Square(1f / 1f),
    Book(10f / 16f),
    ;

    @Composable
    operator fun invoke(
        coil: ImageLoader,
        data: String,
        modifier: Modifier = Modifier,
        contentDescription: String = "",
        shape: Shape = MaterialTheme.shapes.small,
        onClick: (() -> Unit)? = null,
    ) {
        AsyncImageImpl(
            coil = coil,
            model = data,
            contentDescription = contentDescription,
            modifier = modifier
                .aspectRatio(ratio)
                .clip(shape)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            role = Role.Button,
                            onClick = onClick,
                        )
                    } else {
                        Modifier
                    },
                ),
            contentScale = ContentScale.Crop,
        )
    }
}

private val CoverPlaceholderColor = Color(0x1F888888)