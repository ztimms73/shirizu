package org.xtimms.shirizu.core.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.xtimms.shirizu.ui.theme.applyOpacity

@Composable
fun SettingTitle(text: String) {
    Text(
        modifier = Modifier
            .padding(top = 32.dp)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        text = text,
        style = MaterialTheme.typography.displaySmall
    )
}

@Composable
fun SettingItem(title: String, description: String, icon: ImageVector?, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 16.dp)
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.applyOpacity(true)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                AnimatedContent(
                    targetState = description,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (fadeIn()).togetherWith(fadeOut())
                        } else {
                            (fadeIn()).togetherWith(fadeOut())
                        }.using(SizeTransform(clip = false))
                    },
                    label = "Total used"
                ) { targetDescription ->
                    Text(
                        text = targetDescription,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}