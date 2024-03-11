package org.xtimms.tokusho.core.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import org.xtimms.tokusho.ui.theme.TokushoTheme
import java.lang.Integer.MAX_VALUE
import kotlin.math.min

enum class ButtonType { PRIMARY, SECONDARY, TERTIARY, DELETE }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimatedButton(
    modifier: Modifier = Modifier,
    type: ButtonType,
    icon: ImageVector? = null,
    onClick: (() -> Unit) = {},
    onLongClick: (() -> Unit) = {},
) {
    val localDensity = LocalDensity.current
    var minSize by remember { mutableStateOf(MAX_VALUE.dp) }
    var minSizeFloat by remember { mutableStateOf(MAX_VALUE.toFloat()) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState()
    val radius = animateDpAsState(targetValue = if (isPressed.value) 12.dp else minSize / 2)

    val color = when (type) {
        ButtonType.PRIMARY -> MaterialTheme.colorScheme.primaryContainer
        ButtonType.SECONDARY -> MaterialTheme.colorScheme.secondaryContainer
        ButtonType.TERTIARY -> MaterialTheme.colorScheme.tertiaryContainer
        ButtonType.DELETE -> MaterialTheme.colorScheme.errorContainer
    }

    val contentColor = when (type) {
        ButtonType.PRIMARY -> MaterialTheme.colorScheme.onPrimaryContainer
        ButtonType.SECONDARY -> MaterialTheme.colorScheme.onSecondaryContainer
        ButtonType.TERTIARY -> MaterialTheme.colorScheme.onTertiaryContainer
        ButtonType.DELETE -> MaterialTheme.colorScheme.onErrorContainer
    }

    Surface(
        tonalElevation = 10.dp,
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned {
                minSize = with(localDensity) { min(it.size.height, it.size.width).toDp() }
                minSizeFloat = min(it.size.height, it.size.width).toFloat()
            }
            .clip(RoundedCornerShape(radius.value))
    ) {
        Box(
            modifier = Modifier
                .background(color = color)
                .fillMaxSize()
                .clip(RoundedCornerShape(radius.value))
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = ripple(),
                    onClick = { onClick.invoke() },
                    onLongClick = { onLongClick.invoke() },
                ),
            contentAlignment = Alignment.Center
        ) {
            if (icon !== null) {
                Icon(
                    imageVector = icon,
                    tint = contentColor,
                    modifier = Modifier.size(min(minSize * 0.5f, 154.dp)),
                    contentDescription = null,
                )
            }
        }
    }
}

@Preview(name = "Icon")
@Composable
private fun PreviewWithIcon() {
    TokushoTheme {
        AnimatedButton(
            type = ButtonType.PRIMARY,
            icon = Icons.Outlined.Edit
        )
    }
}