package org.xtimms.shirizu.core.components

import android.graphics.Path
import android.view.animation.PathInterpolator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.RssFeed
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SentimentSatisfiedAlt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.DURATION_ENTER
import org.xtimms.shirizu.core.DURATION_EXIT
import org.xtimms.shirizu.core.initialOffset
import org.xtimms.shirizu.core.toEasing
import org.xtimms.shirizu.sections.explore.EXPLORE_DESTINATION
import org.xtimms.shirizu.sections.feed.FEED_DESTINATION
import org.xtimms.shirizu.sections.history.HISTORY_DESTINATION
import org.xtimms.shirizu.sections.search.SEARCH_DESTINATION
import org.xtimms.shirizu.sections.settings.SETTINGS_DESTINATION
import org.xtimms.shirizu.sections.shelf.SHELF_DESTINATION
import org.xtimms.shirizu.ui.theme.ShirizuTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    navController: NavController,
    modifier: Modifier = Modifier,
    backgroundAlphaProvider: () -> Float,
    searchBarColorProvider: () -> Color,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val isVisible by remember {
        derivedStateOf {
            when (navBackStackEntry?.destination?.route) {
                SHELF_DESTINATION, HISTORY_DESTINATION, EXPLORE_DESTINATION,
                null -> true

                else -> false
            }
        }
    }

    val aprilFoolsDay = LocalDate.of(LocalDate.now().year, 4, 1)
    val dtStart = aprilFoolsDay.format(DateTimeFormatter.ISO_DATE)
    val currentDt = LocalDate.now()

    val isAprilFoolsDay = currentDt.format(DateTimeFormatter.ISO_DATE).equals(dtStart)

    val path = Path().apply {
        moveTo(0f, 0f)
        cubicTo(0.05F, 0F, 0.133333F, 0.06F, 0.166666F, 0.4F)
        cubicTo(0.208333F, 0.82F, 0.25F, 1F, 1F, 1F)
    }

    val emphasizePathInterpolator = PathInterpolator(path)
    val emphasizeEasing = emphasizePathInterpolator.toEasing()

    val enterTween = tween<IntOffset>(durationMillis = DURATION_ENTER, easing = emphasizeEasing)
    val exitTween = tween<IntOffset>(durationMillis = DURATION_ENTER, easing = emphasizeEasing)
    val fadeTween = tween<Float>(durationMillis = DURATION_EXIT)

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(
            enterTween,
            initialOffsetX = { -(it * initialOffset).toInt() }) + fadeIn(fadeTween),
        exit = slideOutHorizontally(
            exitTween,
            targetOffsetX = { -(it * initialOffset).toInt() }) + fadeOut(fadeTween)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme
                        .surfaceColorAtElevation(3.dp)
                        .copy(
                            alpha = backgroundAlphaProvider()
                        )
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Card(
                onClick = { navController.navigate(SEARCH_DESTINATION) },
                modifier = modifier
                    .weight(1f)
                    .height(56.dp)
                    .padding(start = 16.dp),
                shape = RoundedCornerShape(50),
                colors = CardDefaults.cardColors().copy(containerColor = searchBarColorProvider()),
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isAprilFoolsDay) Icons.Outlined.SentimentSatisfiedAlt else Icons.Outlined.Search,
                        contentDescription = "search",
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = stringResource(R.string.search),
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(
                modifier = modifier.padding(end = 16.dp),
            ) {
                IconButton(
                    onClick = { navController.navigate(FEED_DESTINATION) },
                    modifier = Modifier.padding(0.dp),
                ) {
                    Icon(
                        Icons.Outlined.RssFeed,
                        contentDescription = stringResource(id = R.string.feed),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(
                    onClick = { navController.navigate(SETTINGS_DESTINATION) },
                    modifier = Modifier.padding(0.dp),
                ) {
                    Icon(
                        Icons.Outlined.Settings,
                        contentDescription = stringResource(id = R.string.settings),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopAppBar(
    title: String,
    actions: @Composable (RowScope.() -> Unit),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateBack: () -> Unit,
) {
    LargeTopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            BackIconButton(onClick = navigateBack)
        },
        actions = actions,
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBarWithChips(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateBack: () -> Unit,
    chips: List<String>
) {
    Column {
        MediumTopAppBar(
            title = { Text(text = title) },
            navigationIcon = {
                BackIconButton(onClick = navigateBack)
            },
            scrollBehavior = scrollBehavior
        )
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(items = chips) {
                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    SuggestionChip(
                        modifier = Modifier.padding(vertical = 4.dp),
                        onClick = { },
                        label = { Text(text = it) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateBack: () -> Unit,
) {
    MediumTopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            BackIconButton(onClick = navigateBack)
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassicTopAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actions: @Composable (RowScope.() -> Unit),
    navigateBack: () -> Unit,
) {
    androidx.compose.material3.TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            BackIconButton(onClick = navigateBack)
        },
        actions = actions,
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun AppBarTitle(
    title: String?,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Column(modifier = modifier) {
        title?.let {
            Text(
                text = it,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        subtitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.basicMarquee(
                    delayMillis = 2_000,
                ),
            )
        }
    }
}