package org.xtimms.shirizu.core.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.xtimms.shirizu.R
import org.xtimms.shirizu.sections.stats.Size
import org.xtimms.shirizu.ui.theme.ShirizuTheme
import org.xtimms.shirizu.utils.composable.secondaryItemAlpha
import org.xtimms.shirizu.utils.material.combineColors

@Composable
fun InfoScreen(
    icon: ImageVector,
    headingText1: String,
    headingText2: String,
    subtitleText: String,
    acceptText: String,
    onAcceptClick: () -> Unit,
    canAccept: Boolean = true,
    rejectText: String? = null,
    onRejectClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {

    val localDensity = LocalDensity.current

    Scaffold(
        bottomBar = {
            val strokeWidth = Dp.Hairline
            val borderColor = MaterialTheme.colorScheme.outline
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .drawBehind {
                        drawLine(
                            borderColor,
                            Offset(0f, 0f),
                            Offset(size.width, 0f),
                            strokeWidth.value,
                        )
                    }
                    .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp,
                    ),
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canAccept,
                    onClick = onAcceptClick,
                ) {
                    Text(text = acceptText)
                }
                if (rejectText != null && onRejectClick != null) {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onRejectClick,
                    ) {
                        Text(text = rejectText)
                    }
                }
            }
        },
    ) { paddingValues ->

        var headerSize by remember { mutableStateOf(Size(0.dp, 0.dp)) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    headerSize = Size(
                        width = with(localDensity) { it.size.width.toDp() },
                        height = with(localDensity) { it.size.height.toDp() }
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            val halfWidth = headerSize.width / 2
            val halfHeight = headerSize.height / 2

            val starColor1 = combineColors(
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.surface,
                0.5f,
            )

            val angleStar1 by rememberInfiniteTransition("angleStar1").animateFloat(
                label = "angleStar1",
                initialValue = -20f,
                targetValue = 20f,
                animationSpec = infiniteRepeatable(tween(5000), RepeatMode.Reverse)
            )

            Icon(
                modifier = Modifier
                    .requiredSize(256.dp)
                    .absoluteOffset(
                        x = halfWidth * 0.7f,
                        y = -halfHeight * 0.6f
                    )
                    .rotate(angleStar1)
                    .zIndex(-1f),
                painter = painterResource(R.drawable.shape_soft_star_1),
                tint = starColor1,
                contentDescription = null,
            )
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(top = 48.dp)
                .padding(horizontal = 16.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .size(48.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            val infiniteTransition by rememberInfiniteTransition("infiniteTransition").animateValue(
                label = "infiniteTransition",
                initialValue = 0,
                targetValue = 2,
                typeConverter = Int.VectorConverter,
                animationSpec = infiniteRepeatable(tween(15000), RepeatMode.Restart)
            )
            val heading = when (infiniteTransition) {
                0 -> headingText1
                1 -> headingText2
                else -> headingText2
            }
            AnimatedContent(targetState = heading, label = "heading animation") {
                Text(
                    text = it,
                    style = MaterialTheme.typography.headlineLarge,
                )
            }
            Text(
                text = subtitleText,
                modifier = Modifier
                    .secondaryItemAlpha()
                    .padding(vertical = 8.dp),
                style = MaterialTheme.typography.titleSmall,
            )

            content()
        }
    }
}

@PreviewLightDark
@Composable
private fun InfoScaffoldPreview() {
    ShirizuTheme {
        InfoScreen(
            icon = Icons.Outlined.Newspaper,
            headingText1 = "Heading 1",
            headingText2 = "Heading 2",
            subtitleText = "Subtitle",
            acceptText = "Accept",
            onAcceptClick = {},
            rejectText = "Reject",
            onRejectClick = {},
        ) {
            Text("Hello world")
        }
    }
}