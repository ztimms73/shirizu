package org.xtimms.shirizu

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RssFeed
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.util.fastForEach
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import coil.ImageLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.xtimms.shirizu.core.components.AppBar
import org.xtimms.shirizu.core.components.AppBarActions
import org.xtimms.shirizu.core.components.AppToolbar
import org.xtimms.shirizu.core.components.Scaffold
import org.xtimms.shirizu.core.components.icons.Creation
import org.xtimms.shirizu.core.logs.FileLogger
import org.xtimms.shirizu.core.onboarding.OnboardingScreen
import org.xtimms.shirizu.core.prefs.AppSettings
import org.xtimms.shirizu.core.scrobbling.services.kitsu.data.KitsuRepository
import org.xtimms.shirizu.core.scrobbling.services.shikimori.data.ShikimoriRepository
import org.xtimms.shirizu.core.ui.dialogs.UpdateDialogImpl
import org.xtimms.shirizu.core.updates.Updater
import org.xtimms.shirizu.sections.explore.ExploreTab
import org.xtimms.shirizu.sections.feed.FeedScreen
import org.xtimms.shirizu.sections.history.HistoryTab
import org.xtimms.shirizu.sections.library.LibraryTab
import org.xtimms.shirizu.sections.onboarding.OnboardingScreen
import org.xtimms.shirizu.sections.search.SearchTab
import org.xtimms.shirizu.sections.settings.SettingsScreen
import org.xtimms.shirizu.sections.shelf.ShelfTab
import org.xtimms.shirizu.sections.suggestions.SuggestionsScreen
import org.xtimms.shirizu.ui.theme.ShirizuTheme
import org.xtimms.shirizu.utils.lang.DefaultNavigatorScreenTransition
import org.xtimms.shirizu.utils.lang.NoLiftingAppBarScreen
import org.xtimms.shirizu.utils.lang.Screen
import org.xtimms.shirizu.utils.lang.isTabletUi
import org.xtimms.shirizu.utils.lang.materialSharedAxisX
import org.xtimms.shirizu.utils.system.setLanguage
import org.xtimms.shirizu.utils.system.suspendToast
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val isReady: MutableState<Boolean> = mutableStateOf(false)
    private val isDone: MutableState<Boolean> = mutableStateOf(false)

    private var navigator: Navigator? = null

    @Inject
    lateinit var coil: ImageLoader

    @Inject
    lateinit var loggers: Set<@JvmSuppressWildcards FileLogger>

    @Inject
    lateinit var shikimoriRepository: ShikimoriRepository

    @Inject
    lateinit var kitsuRepository: KitsuRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { !isDone.value }
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (!isTaskRoot) {
            finish()
            return
        }

        runBlocking {
            if (Build.VERSION.SDK_INT < 33) {
                setLanguage(AppSettings.getLocaleFromPreference())
            }
        }

        setContent {
            val context = LocalContext.current

            val scope = rememberCoroutineScope()
            var updateJob: Job? = null
            var latestRelease by remember { mutableStateOf(Updater.LatestRelease()) }
            var showUpdateDialog by rememberSaveable { mutableStateOf(false) }
            var currentDownloadStatus by remember { mutableStateOf(Updater.DownloadStatus.NotYet as Updater.DownloadStatus) }

            val settings =
                rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    Updater.installLatestApk(context)
                }

            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { result ->
                if (result) {
                    Updater.installLatestApk(context)
                } else {
                    if (!context.packageManager.canRequestPackageInstalls())
                        settings.launch(
                            Intent(
                                Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                                Uri.parse("package:${context.packageName}"),
                            )
                        )
                    else
                        Updater.installLatestApk(context)
                }
            }

            LaunchedEffect(Unit) {
                isReady.value = true
            }
            if (isReady.value) {
                CompositionLocalProvider(
                    LocalImageLoader provides coil,
                    LocalLoggers provides loggers,
                    LocalShikimoriRepository provides shikimoriRepository,
                    LocalKitsuRepository provides kitsuRepository
                ) {
                    SettingsProvider {
                        ShirizuTheme(
                            darkTheme = LocalDarkTheme.current.isDarkTheme(),
                            isDynamicColorEnabled = LocalDynamicColorSwitch.current,
                            isHighContrastModeEnabled = LocalDarkTheme.current.isHighContrastModeEnabled,
                        ) {
                            Navigator(
                                screen = MainScreen,
                                disposeBehavior = NavigatorDisposeBehavior(
                                    disposeNestedNavigators = false,
                                    disposeSteps = true
                                ),
                            ) { navigator ->
                                LaunchedEffect(navigator) {
                                    this@MainActivity.navigator = navigator
                                }

                                val scaffoldInsets =
                                    WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal)
                                Scaffold(
                                    contentWindowInsets = scaffoldInsets,
                                ) { contentPadding ->
                                    // Consume insets already used by app state banners
                                    Box(
                                        modifier = Modifier
                                            .padding(contentPadding)
                                            .consumeWindowInsets(contentPadding),
                                    ) {
                                        // Shows current screen
                                        DefaultNavigatorScreenTransition(navigator = navigator)
                                    }
                                }

                                // ShowOnboarding()
                            }

                            LaunchedEffect(Unit) {
                                isDone.value = true
                            }

                            LaunchedEffect(Unit) {
                                if (!AppSettings.isAutoUpdateEnabled())
                                    return@LaunchedEffect
                                launch(Dispatchers.IO) {
                                    runCatching {
                                        Updater.checkForUpdate(context)?.let {
                                            latestRelease = it
                                            showUpdateDialog = true
                                        }
                                    }.onFailure {
                                        it.printStackTrace()
                                    }
                                }
                            }

                            if (showUpdateDialog) {
                                UpdateDialogImpl(
                                    onDismissRequest = {
                                        showUpdateDialog = false
                                        updateJob?.cancel()
                                    },
                                    title = latestRelease.name.toString(),
                                    onConfirmUpdate = {
                                        updateJob = scope.launch(Dispatchers.IO) {
                                            runCatching {
                                                Updater.downloadApk(context, latestRelease)
                                                    .collect { downloadStatus ->
                                                        currentDownloadStatus = downloadStatus
                                                        if (downloadStatus is Updater.DownloadStatus.Finished) {
                                                            launcher.launch(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                                                        }
                                                    }
                                            }.onFailure {
                                                it.printStackTrace()
                                                currentDownloadStatus =
                                                    Updater.DownloadStatus.NotYet
                                                context.suspendToast(R.string.app_update_failed)
                                                return@launch
                                            }
                                        }
                                    },
                                    releaseNote = latestRelease.body.toString(),
                                    downloadStatus = currentDownloadStatus
                                )
                            }
                        }
                    }
                }
            }
        }
        putDataToExtras(intent)
    }

    @Composable
    private fun ShowOnboarding() {
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            if (navigator.lastItem !is OnboardingScreen) {
                navigator.push(OnboardingScreen())
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        putDataToExtras(intent)
        super.onNewIntent(intent)
    }

    private fun putDataToExtras(intent: Intent?) {
        intent?.putExtra(EXTRA_DATA, intent.data)
    }

    companion object {
        private const val TAG = "MainActivity"
        const val EXTRA_DATA = "data"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
object MainScreen : Screen() {

    private val librarySearchEvent = Channel<String>()
    private val openTabEvent = Channel<Tab>()
    private val showBottomNavEvent = Channel<Boolean>()

    private const val TabNavigatorKey = "HomeTabs"

    private val tabs = listOf(
        LibraryTab(),
        // ShelfTab,
        // HistoryTab,
        ExploreTab(),
        SearchTab
    )

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        TabNavigator(
            tab = LibraryTab(),
            key = TabNavigatorKey,
        ) { tabNavigator ->
            // Provide usable navigator to content screen
            CompositionLocalProvider(LocalNavigator provides navigator) {
                Scaffold(
                    topBar = { scrollBehavior ->
                        if (!isTabletUi()) {
                            AppToolbar(
                                actions = {
                                    AppBarActions(
                                        persistentListOf(
                                            AppBar.Action(
                                                title = stringResource(R.string.suggestions),
                                                icon = Icons.Outlined.Creation,
                                                onClick = {
                                                    navigator.push(SuggestionsScreen)
                                                },
                                            ),
                                            AppBar.Action(
                                                title = stringResource(R.string.feed),
                                                icon = Icons.Outlined.RssFeed,
                                                onClick = {
                                                    navigator.push(FeedScreen)
                                                },
                                            ),
                                            AppBar.Action(
                                                title = stringResource(R.string.settings),
                                                icon = Icons.Outlined.Settings,
                                                onClick = {
                                                    navigator.push(SettingsScreen)
                                                },
                                            ),
                                        ),
                                    )
                                },
                                scrollBehavior = if (tabNavigator.current is NoLiftingAppBarScreen) {
                                    null
                                } else scrollBehavior
                            )
                        }
                    },
                    startBar = {
                        if (isTabletUi()) {
                            NavigationRail {
                                tabs.fastForEach {
                                    NavigationRailItem(it)
                                }
                            }
                        }
                    },
                    bottomBar = {
                        if (!isTabletUi()) {
                            val bottomNavVisible by produceState(initialValue = true) {
                                showBottomNavEvent.receiveAsFlow().collectLatest { value = it }
                            }
                            AnimatedVisibility(
                                visible = bottomNavVisible,
                                enter = expandVertically(),
                                exit = shrinkVertically(),
                            ) {
                                NavigationBar {
                                    tabs.fastForEach {
                                        NavigationBarItem(it)
                                    }
                                }
                            }
                        }
                    },
                    contentWindowInsets = WindowInsets(0),
                ) { contentPadding ->
                    Box(
                        modifier = Modifier
                            .padding(contentPadding)
                            .consumeWindowInsets(contentPadding),
                    ) {
                        AnimatedContent(
                            targetState = tabNavigator.current,
                            transitionSpec = {
                                materialSharedAxisX(forward = true)
                            },
                            label = "tabContent",
                        ) {
                            tabNavigator.saveableState(key = "currentTab", it) {
                                it.Content()
                            }
                        }
                    }
                }
            }

            val goToLibraryTab = { tabNavigator.current = LibraryTab() }
            BackHandler(
                enabled = tabNavigator.current != LibraryTab(),
                onBack = goToLibraryTab,
            )

            LaunchedEffect(Unit) {
                launch {
                    openTabEvent.receiveAsFlow().collectLatest {
                        tabNavigator.current = when (it) {
                            is Tab.Library -> LibraryTab()
                            // is Tab.Shelf -> ShelfTab
                            // is Tab.History -> HistoryTab
                            is Tab.Explore -> ExploreTab()
                            is Tab.Search -> SearchTab
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun RowScope.NavigationBarItem(tab: org.xtimms.shirizu.utils.lang.Tab) {
        val tabNavigator = LocalTabNavigator.current
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val selected = tabNavigator.current::class == tab::class
        NavigationBarItem(
            selected = selected,
            onClick = {
                if (!selected) {
                    tabNavigator.current = tab
                } else {
                    scope.launch { tab.onReselect(navigator) }
                }
            },
            icon = { NavigationIconItem(tab) },
            label = {
                Text(
                    text = tab.options.title,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            alwaysShowLabel = true,
        )
    }

    @Composable
    fun NavigationRailItem(tab: org.xtimms.shirizu.utils.lang.Tab) {
        val tabNavigator = LocalTabNavigator.current
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val selected = tabNavigator.current::class == tab::class
        NavigationRailItem(
            selected = selected,
            onClick = {
                if (!selected) {
                    tabNavigator.current = tab
                } else {
                    scope.launch { tab.onReselect(navigator) }
                }
            },
            icon = { NavigationIconItem(tab) },
            label = {
                Text(
                    text = tab.options.title,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            alwaysShowLabel = true,
        )
    }

    @Composable
    private fun NavigationIconItem(tab: org.xtimms.shirizu.utils.lang.Tab) {
        Icon(
            painter = tab.options.icon!!,
            contentDescription = tab.options.title,
            // TODO: https://issuetracker.google.com/u/0/issues/316327367
            tint = LocalContentColor.current,
        )
    }

    suspend fun showBottomNav(show: Boolean) {
        showBottomNavEvent.send(show)
    }

    sealed interface Tab {
        data object Library : Tab
        // data object Shelf : Tab
        // data object History : Tab
        data object Explore : Tab
        data object Search : Tab
    }
}