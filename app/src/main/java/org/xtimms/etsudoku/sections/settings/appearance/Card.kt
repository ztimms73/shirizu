package org.xtimms.etsudoku.sections.settings.appearance

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.xtimms.etsudoku.R
import org.xtimms.etsudoku.ui.theme.EtsudokuTheme

@Composable
fun MangaCard(
    modifier: Modifier = Modifier,
    title: String = "Ookami to Koushinryou",
    author: String = "Hasekura Isuna",
    thumbnailUrl: Any = "",
    showCancelButton: Boolean = false,
    onCancel: () -> Unit = {},
    onClick: () -> Unit = {},
    progress: Float = 75f,
) {
    ElevatedCard(
        modifier = modifier
            .height(136.dp)
            .fillMaxWidth(),
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                modifier = Modifier
                    .padding()
                    .fillMaxHeight()
                    .clip(MaterialTheme.shapes.small),
                painter = painterResource(id = R.drawable.ookami),
                contentDescription = null
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (author != "null") Text(
                    modifier = Modifier.padding(top = 3.dp),
                    text = author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun MangaCardPreview() {
    EtsudokuTheme {
        MangaCard(
            thumbnailUrl = "https://spice-and-wolf.com/special/img/visual_january.jpg"
        )
    }
}