package org.xtimms.shirizu.sections.stats

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.koitharu.kotatsu.parsers.model.MangaChapter
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.shirizu.LocalBottomSheetScrollState
import org.xtimms.shirizu.LocalWindowInsets
import org.xtimms.shirizu.R
import org.xtimms.shirizu.sections.stats.categories.CategoriesChart
import org.xtimms.shirizu.ui.theme.ShirizuTheme
import org.xtimms.shirizu.utils.material.combineColors

data class Size(val width: Dp, val height: Dp)

const val STATS_DESTINATION = "stats"

@Composable
fun StatsView(
    navigateBack: () -> Unit,
) {

    val localDensity = LocalDensity.current
    val localBottomSheetScrollState = LocalBottomSheetScrollState.current

    val scrollState = rememberScrollState()
    val scroll = with(localDensity) { scrollState.value.toDp() }

    val navigationBarHeight =
        LocalWindowInsets.current.calculateBottomPadding().coerceAtLeast(16.dp)

    Surface(Modifier.height(IntrinsicSize.Min)) {
        Column(
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                var headerSize by remember { mutableStateOf(Size(0.dp, 0.dp)) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = localBottomSheetScrollState.topPadding.coerceAtLeast(36.dp))
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

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .absoluteOffset(y = scroll * 0.25f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(Modifier.height(36.dp))
                        Text(
                            text = stringResource(R.string.reading_based_stats),
                            style = MaterialTheme.typography.headlineLarge,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(id = R.string.reading_based_stats_desc),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(64.dp))
                    }

                    val starColor1 = combineColors(
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.surface,
                        0.5f,
                    )

                    val starColor2 = combineColors(
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

                    val angleStar2 by rememberInfiniteTransition("angleStar2").animateFloat(
                        label = "angleStar2",
                        initialValue = -50f,
                        targetValue = 50f,
                        animationSpec = infiniteRepeatable(tween(9000), RepeatMode.Reverse)
                    )

                    Icon(
                        modifier = Modifier
                            .requiredSize(256.dp)
                            .absoluteOffset(
                                x = halfWidth * 0.7f,
                                y = -halfHeight * 0.6f + scroll * 0.35f
                            )
                            .rotate(angleStar1)
                            .zIndex(-1f),
                        painter = painterResource(R.drawable.shape_soft_star_1),
                        tint = starColor1,
                        contentDescription = null,
                    )
                    Icon(
                        modifier = Modifier
                            .requiredSize(256.dp)
                            .absoluteOffset(
                                x = -halfWidth * 0.7f,
                                y = halfHeight * 0.6f + scroll * 0.6f
                            )
                            .rotate(angleStar2)
                            .zIndex(-1f),
                        painter = painterResource(R.drawable.shape_soft_star_2),
                        tint = starColor2,
                        contentDescription = null,
                    )
                }
                Column(Modifier.fillMaxWidth()) {
                    TimeCard()
                    Spacer(modifier = Modifier.height(1000.dp))
                }
            }

            Spacer(
                Modifier
                    .height(60.dp + navigationBarHeight)
                    .fillMaxWidth())
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = WindowInsets.navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                ),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(60.dp)
                    .padding(horizontal = 16.dp),
                onClick = {
                    navigateBack()
                }
            ) {
                Text(
                    text = stringResource(R.string.got_it),
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ShirizuTheme {
        StatsView(
            navigateBack = { }
        )
    }
}