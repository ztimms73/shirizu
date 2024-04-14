package org.xtimms.shirizu.sections.details

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.model.MangaState
import org.koitharu.kotatsu.parsers.model.RATING_UNKNOWN
import org.xtimms.shirizu.LocalWindowWidthState
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.HapticFeedback.slightHapticFeedback
import org.xtimms.shirizu.core.components.DetailsToolbar
import org.xtimms.shirizu.core.components.MangaHorizontalItem
import org.xtimms.shirizu.core.parser.favicon.faviconUri
import org.xtimms.shirizu.core.prefs.AppSettings
import org.xtimms.shirizu.core.prefs.AppSettings.getBoolean
import org.xtimms.shirizu.core.prefs.AppSettings.updateBoolean
import org.xtimms.shirizu.core.prefs.CELLULAR_DOWNLOAD
import org.xtimms.shirizu.core.prefs.CONFIGURE
import org.xtimms.shirizu.core.prefs.NOTIFICATION
import org.xtimms.shirizu.core.ui.dialogs.MeteredNetworkDialog
import org.xtimms.shirizu.core.ui.dialogs.NotificationPermissionDialog
import org.xtimms.shirizu.utils.lang.toNavArgument
import org.xtimms.shirizu.utils.system.toast

const val MANGA_ID_ARGUMENT = "{mangaId}"
const val DETAILS_DESTINATION = "details/?mangaId=$MANGA_ID_ARGUMENT"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun DetailsView(
    coil: ImageLoader,
    mangaId: Long,
    viewModel: DetailsViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToFullImage: (String) -> Unit,
    navigateToDetails: (Long) -> Unit,
    navigateToSource: (MangaSource) -> Unit,
    navigateToReader: () -> Unit
) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val view = LocalView.current
    val useDialog = LocalWindowWidthState.current != WindowWidthSizeClass.Compact

    val chapterListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    var openCategoriesBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showDownloadDialog by rememberSaveable { mutableStateOf(false) }
    var showMeteredNetworkDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }
    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS) { isGranted: Boolean ->
            showNotificationDialog = false
            if (!isGranted) {
                context.toast(R.string.permission_denied)
            }
        }
    } else null

    val checkNetworkOrDownload = {
        if (!AppSettings.isNetworkAvailableForDownload()) {
            showMeteredNetworkDialog = true
        } else {
            scope.launch { snackbarHostState.showSnackbar("Downloading...") }
        }
    }

    val storagePermission = rememberPermissionState(
        permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) { b: Boolean ->
        if (b) {
            checkNetworkOrDownload()
        } else {
            context.toast(R.string.permission_denied)
        }
    }

    val checkPermissionOrDownload = {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q || storagePermission.status == PermissionStatus.Granted) {
            checkNetworkOrDownload()
        } else {
            storagePermission.launchPermissionRequest()
        }
    }

    val uriHandler = LocalUriHandler.current
    fun openUrl(url: String) {
        uriHandler.openUri(url)
    }

    val chapters by viewModel.chapters.collectAsStateWithLifecycle(emptyList())
    val relatedManga by viewModel.relatedManga.collectAsStateWithLifecycle(emptyList())
    val readingTime by viewModel.readingTime.collectAsStateWithLifecycle(null)
    val favouriteCategories by viewModel.favouriteCategories.collectAsStateWithLifecycle()
    val details by viewModel.details.collectAsStateWithLifecycle(null)
    val historyInfo by viewModel.historyInfo.collectAsStateWithLifecycle()
    val sheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val downloadCallback: () -> Unit = {
        view.slightHapticFeedback()
        if (NOTIFICATION.getBoolean() && notificationPermission?.status?.isGranted == false) {
            showNotificationDialog = true
        }
        if (CONFIGURE.getBoolean()) {
            showDownloadDialog = true
            scope.launch {
                delay(50)
                sheetState.show()
            }
        } else {
            checkPermissionOrDownload()
        }
    }

    if (showNotificationDialog) {
        NotificationPermissionDialog(onDismissRequest = {
            showNotificationDialog = false
            NOTIFICATION.updateBoolean(false)
        }, onPermissionGranted = {
            notificationPermission?.launchPermissionRequest()
        })
    }

    if (showMeteredNetworkDialog) {
        MeteredNetworkDialog(
            onDismissRequest = { showMeteredNetworkDialog = false },
            onAllowOnceConfirm = {
                scope.launch { snackbarHostState.showSnackbar("Downloading...") }
                showMeteredNetworkDialog = false
            },
            onAllowAlwaysConfirm = {
                scope.launch { snackbarHostState.showSnackbar("Downloading...") }
                CELLULAR_DOWNLOAD.updateBoolean(true)
                showMeteredNetworkDialog = false
            })
    }

    LaunchedEffect(mangaId) {
        if (details == null) viewModel.doLoad(mangaId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { e ->
            when (e) {
                DetailsViewModel.Event.InternalError ->
                    snackbarHostState.showSnackbar(context.getString(R.string.error_occured))
            }
        }
    }

    Scaffold(
        topBar = {
            val isFirstItemVisible by remember {
                derivedStateOf { chapterListState.firstVisibleItemIndex == 0 }
            }
            val isFirstItemScrolled by remember {
                derivedStateOf { chapterListState.firstVisibleItemScrollOffset > 0 }
            }
            val animatedTitleAlpha by animateFloatAsState(
                if (!isFirstItemVisible) 1f else 0f,
                label = "Top Bar Title",
            )
            val animatedBgAlpha by animateFloatAsState(
                if (!isFirstItemVisible || isFirstItemScrolled) 1f else 0f,
                label = "Top Bar Background",
            )
            DetailsToolbar(
                title = viewModel.details.value?.toManga()?.title.orEmpty(),
                titleAlphaProvider = { animatedTitleAlpha },
                backgroundAlphaProvider = { animatedBgAlpha },
                navigateBack = { navigateBack() },
                navigateToWebBrowser = { openUrl(viewModel.details.value?.toManga()?.publicUrl.orEmpty()) },
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { contentPadding ->
        val topPadding = contentPadding.calculateTopPadding()
        val layoutDirection = LocalLayoutDirection.current
        val relatedMangaListState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            state = chapterListState,
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding() - 60.dp,
                start = contentPadding.calculateStartPadding(layoutDirection),
                end = contentPadding.calculateEndPadding(layoutDirection),
                bottom = contentPadding.calculateBottomPadding(),
            ),
        ) {
            val manga = details?.toManga()
            item(
                key = DetailsViewItem.INFO_BOX,
                contentType = DetailsViewItem.INFO_BOX
            ) {
                DetailsInfoBox(
                    coil = coil,
                    imageUrl = manga?.largeCoverUrl ?: manga?.coverUrl.orEmpty(),
                    favicon = manga?.source?.faviconUri() ?: Uri.EMPTY,
                    title = manga?.title.orEmpty(),
                    altTitle = manga?.altTitle.orEmpty(),
                    author = manga?.author.orEmpty(),
                    isNsfw = manga?.isNsfw ?: true,
                    state = manga?.state ?: MangaState.FINISHED,
                    source = manga?.source ?: MangaSource.DUMMY,
                    isTabletUi = false,
                    appBarPadding = topPadding,
                    onCoverClick = {
                        navigateToFullImage(
                            arrayOf(
                                manga?.largeCoverUrl ?: manga?.coverUrl.orEmpty(),
                            ).toNavArgument()
                        )
                    },
                    historyInfo = historyInfo,
                    readingTime = readingTime,
                    isInShelf = favouriteCategories,
                    onAddToShelfClicked = {
                        openCategoriesBottomSheet = !openCategoriesBottomSheet
                    },
                    onSourceClicked = {
                        navigateToSource(
                            manga?.source ?: MangaSource.DUMMY
                        )
                    },
                    onDownloadClick = downloadCallback
                )
            }

            item(
                key = DetailsViewItem.DESCRIPTION_WITH_TAG,
                contentType = DetailsViewItem.DESCRIPTION_WITH_TAG,
            ) {
                ExpandableMangaDescription(
                    defaultExpandState = true,
                    description = viewModel.details.value?.toManga()?.description,
                    tagsProvider = { viewModel.details.value?.toManga()?.tags },
                    onTagSearch = { },
                    onCopyTagToClipboard = { },
                )
            }

            item {
                AnimatedVisibility(
                    visible = relatedManga.isNotEmpty() && AppSettings.isRelatedMangaEnabled(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column {
                        Text(
                            modifier = Modifier.padding(start = 16.dp, end = 8.dp),
                            text = stringResource(id = R.string.related_manga),
                            style = MaterialTheme.typography.titleLarge
                        )
                        LazyRow(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .sizeIn(minHeight = 100.dp),
                            state = relatedMangaListState,
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            flingBehavior = rememberSnapFlingBehavior(lazyListState = relatedMangaListState)
                        ) {
                            items(
                                items = relatedManga,
                                key = { it.id },
                                contentType = { it }
                            ) {
                                MangaHorizontalItem(
                                    coil = coil,
                                    manga = it,
                                    onClick = { navigateToDetails(it.id) },
                                    onLongClick = { })
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(16.dp))
                    }
                }
            }

            item {
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 8.dp, bottom = 8.dp),
                    text = stringResource(id = R.string.chapters),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(
                items = chapters
            ) {
                ChapterListItem(
                    title = it.chapter.name,
                    date = it.chapter.uploadDate,
                    scanlator = it.chapter.scanlator,
                    read = !it.isUnread,
                    bookmark = false,
                    selected = false,
                    onLongClick = { /*TODO*/ },
                    onClick = { navigateToReader() }
                )
            }
        }
    }

    DownloadSettingDialog(
        useDialog = useDialog,
        showDialog = showDownloadDialog,
        sheetState = sheetState,
        onDownloadConfirm = { checkPermissionOrDownload() },
        onDismissRequest = {
            if (!useDialog) {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    showDownloadDialog = false
                }
            } else {
                showDownloadDialog = false
            }
        }
    )

    if (openCategoriesBottomSheet) {
        val windowInsets = WindowInsets(0)

        ModalBottomSheet(
            onDismissRequest = { openCategoriesBottomSheet = false },
            windowInsets = windowInsets
        ) {
            Text(text = "Hello MBS")
            Spacer(modifier = Modifier.height(1000.dp))
        }
    }
}