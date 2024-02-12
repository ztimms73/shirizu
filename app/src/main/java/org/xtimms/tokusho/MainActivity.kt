package org.xtimms.tokusho

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.xtimms.tokusho.core.Navigation
import org.xtimms.tokusho.core.components.BottomNavBar
import org.xtimms.tokusho.core.components.TopAppBar
import org.xtimms.tokusho.core.logs.FileLogger
import org.xtimms.tokusho.ui.theme.TokushoTheme
import org.xtimms.tokusho.utils.lang.processLifecycleScope
import javax.inject.Inject

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var coil: ImageLoader

    @Inject
    lateinit var loggers: Set<@JvmSuppressWildcards FileLogger>

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (!isTaskRoot) {
            finish()
            return
        }

        setContent {
            val navController = rememberNavController()
            val windowSizeClass = calculateWindowSizeClass(this)
            val isCompactScreen = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
            SettingsProvider {
                TokushoTheme(
                    darkTheme = LocalDarkTheme.current.isDarkTheme(),
                    isDynamicColorEnabled = LocalDynamicColorSwitch.current,
                    isHighContrastModeEnabled = LocalDarkTheme.current.isHighContrastModeEnabled,
                ) {
                    MainView(
                        coil = coil,
                        loggers = loggers,
                        isCompactScreen = isCompactScreen,
                        navController = navController
                    )
                }
            }
        }
        putDataToExtras(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        putDataToExtras(intent)
        super.onNewIntent(intent)
    }

    private fun putDataToExtras(intent: Intent?) {
        intent?.putExtra(EXTRA_DATA, intent.data)
    }

    companion object {
        private const val TAG = "MainActivity"
        const val EXTRA_DATA = "data"

        fun setLanguage(locale: String) {
            Log.d(TAG, "setLanguage: $locale")
            val localeListCompat =
                if (locale.isEmpty()) LocaleListCompat.getEmptyLocaleList()
                else LocaleListCompat.forLanguageTags(locale)
            processLifecycleScope.launch(Dispatchers.Main) {
                AppCompatDelegate.setApplicationLocales(localeListCompat)
            }
        }

    }
}

@Composable
fun MainView(
    coil: ImageLoader,
    loggers: Set<FileLogger>,
    isCompactScreen: Boolean,
    navController: NavHostController,
) {
    val density = LocalDensity.current

    val bottomBarState = remember { mutableStateOf(true) }
    var topBarHeightPx by remember { mutableFloatStateOf(0f) }
    val topBarOffsetY = remember { Animatable(0f) }

    val scroll: LazyGridState = rememberLazyGridState()

    Scaffold(
        topBar = {
            if (isCompactScreen) {
                val isScrolled by remember {
                    derivedStateOf { scroll.firstVisibleItemScrollOffset > 0 }
                }
                val animatedBgAlpha by animateFloatAsState(
                    if (isScrolled) 1f else 0f,
                    label = "Top Bar Background",
                )
                TopAppBar(
                    navController = navController,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(0.dp, 16.dp),
                    backgroundAlphaProvider = { animatedBgAlpha },
                )
            }
        },
        bottomBar = {
            if (isCompactScreen) {
                BottomNavBar(
                    navController = navController,
                    bottomBarState = bottomBarState,
                    topBarOffsetY = topBarOffsetY,
                )
            }
        },
        contentWindowInsets = WindowInsets.systemBars
            .only(WindowInsetsSides.Horizontal)
    ) { padding ->
        if (!isCompactScreen) {
            val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
            Row(
                modifier = Modifier.padding(padding)
            ) {
                Navigation(
                    coil = coil,
                    loggers = loggers,
                    navController = navController,
                    isCompactScreen = false,
                    modifier = Modifier,
                    padding = PaddingValues(
                        start = padding.calculateStartPadding(LocalLayoutDirection.current),
                        top = systemBarsPadding.calculateTopPadding(),
                        end = padding.calculateEndPadding(LocalLayoutDirection.current),
                        bottom = systemBarsPadding.calculateBottomPadding()
                    ),
                    topBarHeightPx = topBarHeightPx,
                    listState = scroll
                )
            }
        } else {
            LaunchedEffect(padding) {
                topBarHeightPx = density.run { padding.calculateTopPadding().toPx() }
            }
            Navigation(
                coil = coil,
                loggers = loggers,
                navController = navController,
                isCompactScreen = true,
                modifier = Modifier.padding(
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current),
                ),
                padding = padding,
                topBarHeightPx = topBarHeightPx,
                listState = scroll
            )
        }
    }
}