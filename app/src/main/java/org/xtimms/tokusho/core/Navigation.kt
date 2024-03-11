package org.xtimms.tokusho.core

import android.graphics.Path
import android.view.animation.PathInterpolator
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import coil.ImageLoader
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.tokusho.core.logs.FileLogger
import org.xtimms.tokusho.core.motion.materialSharedAxisXIn
import org.xtimms.tokusho.core.motion.materialSharedAxisXOut
import org.xtimms.tokusho.sections.details.DETAILS_DESTINATION
import org.xtimms.tokusho.sections.details.DetailsView
import org.xtimms.tokusho.sections.details.FULL_POSTER_DESTINATION
import org.xtimms.tokusho.sections.details.FullImageView
import org.xtimms.tokusho.sections.details.MANGA_ID_ARGUMENT
import org.xtimms.tokusho.sections.details.PICTURES_ARGUMENT
import org.xtimms.tokusho.sections.explore.ExploreView
import org.xtimms.tokusho.sections.feed.FEED_DESTINATION
import org.xtimms.tokusho.sections.feed.FeedView
import org.xtimms.tokusho.sections.history.HistoryView
import org.xtimms.tokusho.sections.list.LIST_DESTINATION
import org.xtimms.tokusho.sections.list.MangaListView
import org.xtimms.tokusho.sections.list.PROVIDER_ARGUMENT
import org.xtimms.tokusho.sections.search.SEARCH_DESTINATION
import org.xtimms.tokusho.sections.search.SearchHostView
import org.xtimms.tokusho.sections.settings.SETTINGS_DESTINATION
import org.xtimms.tokusho.sections.settings.SettingsView
import org.xtimms.tokusho.sections.settings.about.ABOUT_DESTINATION
import org.xtimms.tokusho.sections.settings.about.AboutView
import org.xtimms.tokusho.sections.settings.about.LICENSES_DESTINATION
import org.xtimms.tokusho.sections.settings.about.LICENSE_CONTENT_ARGUMENT
import org.xtimms.tokusho.sections.settings.about.LICENSE_DESTINATION
import org.xtimms.tokusho.sections.settings.about.LICENSE_NAME_ARGUMENT
import org.xtimms.tokusho.sections.settings.about.LICENSE_WEBSITE_ARGUMENT
import org.xtimms.tokusho.sections.settings.about.LicenseView
import org.xtimms.tokusho.sections.settings.about.OpenSourceLicensesView
import org.xtimms.tokusho.sections.settings.about.UPDATES_DESTINATION
import org.xtimms.tokusho.sections.settings.about.UpdateView
import org.xtimms.tokusho.sections.settings.advanced.ADVANCED_DESTINATION
import org.xtimms.tokusho.sections.settings.advanced.AdvancedView
import org.xtimms.tokusho.sections.settings.appearance.APPEARANCE_DESTINATION
import org.xtimms.tokusho.sections.settings.appearance.AppearanceView
import org.xtimms.tokusho.sections.settings.appearance.DARK_THEME_DESTINATION
import org.xtimms.tokusho.sections.settings.appearance.DarkThemeView
import org.xtimms.tokusho.sections.settings.appearance.LANGUAGES_DESTINATION
import org.xtimms.tokusho.sections.settings.appearance.LanguagesView
import org.xtimms.tokusho.sections.settings.backup.BACKUP_RESTORE_DESTINATION
import org.xtimms.tokusho.sections.settings.backup.BackupRestoreView
import org.xtimms.tokusho.sections.settings.backup.RESTORE_ARGUMENT
import org.xtimms.tokusho.sections.settings.backup.RESTORE_DESTINATION
import org.xtimms.tokusho.sections.settings.backup.RestoreItemsView
import org.xtimms.tokusho.sections.settings.network.NETWORK_DESTINATION
import org.xtimms.tokusho.sections.settings.network.NetworkView
import org.xtimms.tokusho.sections.settings.shelf.SHELF_SETTINGS_DESTINATION
import org.xtimms.tokusho.sections.settings.shelf.ShelfSettingsView
import org.xtimms.tokusho.sections.settings.shelf.categories.CATEGORIES_DESTINATION
import org.xtimms.tokusho.sections.settings.shelf.categories.CategoriesView
import org.xtimms.tokusho.sections.settings.sources.SOURCES_DESTINATION
import org.xtimms.tokusho.sections.settings.sources.SourcesView
import org.xtimms.tokusho.sections.settings.sources.catalog.CATALOG_DESTINATION
import org.xtimms.tokusho.sections.settings.sources.catalog.SourcesCatalogView
import org.xtimms.tokusho.sections.settings.storage.STORAGE_DESTINATION
import org.xtimms.tokusho.sections.settings.storage.StorageView
import org.xtimms.tokusho.sections.shelf.ShelfView
import org.xtimms.tokusho.sections.stats.STATS_DESTINATION
import org.xtimms.tokusho.sections.stats.StatsView
import org.xtimms.tokusho.utils.StringArrayNavType
import org.xtimms.tokusho.utils.lang.removeFirstAndLast

const val DURATION_ENTER = 400
const val DURATION_EXIT = 200
const val initialOffset = 0.10f

fun PathInterpolator.toEasing(): Easing {
    return Easing { f -> this.getInterpolation(f) }
}

@Composable
fun Navigation(
    coil: ImageLoader,
    loggers: Set<FileLogger>,
    navController: NavHostController,
    isCompactScreen: Boolean,
    modifier: Modifier,
    padding: PaddingValues,
    topBarHeightPx: Float,
    listState: LazyGridState,
) {

    val navigateBack: () -> Unit = { navController.popBackStack() }

    val navigateToLicense: (String, String?, String?) -> Unit = { name, website, content ->
        navController.navigate(
            LICENSE_DESTINATION
                .replace(LICENSE_NAME_ARGUMENT, name)
                .replace(LICENSE_WEBSITE_ARGUMENT, website.orEmpty())
                .replace(LICENSE_CONTENT_ARGUMENT, content ?: "No license text")
        )
    }

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
    val fadeSpec = fadeTween

    NavHost(
        navController = navController,
        startDestination = BottomNavDestination.Shelf.route,
        modifier = modifier,
        enterTransition = { materialSharedAxisXIn(initialOffsetX = { (it * initialOffset).toInt() }) },
        exitTransition = { materialSharedAxisXOut(targetOffsetX = { -(it * initialOffset).toInt() }) },
        popEnterTransition = { materialSharedAxisXIn(initialOffsetX = { -(it * initialOffset).toInt() }) },
        popExitTransition = { materialSharedAxisXOut(targetOffsetX = { (it * initialOffset).toInt() }) }
    ) {

        composable(BottomNavDestination.Shelf.route) {
            ShelfView(
                coil = coil,
                currentPage = { 2 },
                showPageTabs = true,
                padding = padding,
                topBarHeightPx = topBarHeightPx,
                navigateToDetails = {
                    navController.navigate(
                        DETAILS_DESTINATION.replace(MANGA_ID_ARGUMENT, it.toString())
                    )
                },
                onRefresh = { true }
            )
        }

        composable(BottomNavDestination.History.route) {
            HistoryView(
                padding = padding,
                topBarHeightPx = topBarHeightPx,
            )
        }

        composable(BottomNavDestination.Explore.route) {
            ExploreView(
                coil = coil,
                navigateToSource = {
                    navController.navigate(
                        LIST_DESTINATION.replace(PROVIDER_ARGUMENT, it.name)
                    )
                },
                padding = padding,
                topBarHeightPx = topBarHeightPx,
                listState = listState
            )
        }

        composable(SEARCH_DESTINATION) {
            SearchHostView(
                isCompactScreen = isCompactScreen,
                padding = if (isCompactScreen) PaddingValues() else padding,
                navigateBack = navigateBack,
            )
        }

        composable(FEED_DESTINATION) {
            FeedView(
                navigateBack = navigateBack,
                navigateToShelf = { navController.navigate(SHELF_SETTINGS_DESTINATION) }
            )
        }

        composable(SETTINGS_DESTINATION) {
            SettingsView(
                navigateBack = navigateBack,
                navigateToAppearance = { navController.navigate(APPEARANCE_DESTINATION) },
                navigateToAbout = { navController.navigate(ABOUT_DESTINATION) },
                navigateToAdvanced = { navController.navigate(ADVANCED_DESTINATION) },
                navigateToBackupRestoreSettings = {
                    navController.navigate(
                        BACKUP_RESTORE_DESTINATION
                    )
                },
                navigateToMangaSources = { navController.navigate(SOURCES_DESTINATION) },
                navigateToNetwork = { navController.navigate(NETWORK_DESTINATION) },
                navigateToShelfSettings = { navController.navigate(SHELF_SETTINGS_DESTINATION) },
                navigateToStorage = { navController.navigate(STORAGE_DESTINATION) }
            )
        }

        composable(APPEARANCE_DESTINATION) {
            AppearanceView(
                navigateBack = navigateBack,
                navigateToDarkTheme = { navController.navigate(DARK_THEME_DESTINATION) },
                navigateToLanguages = { navController.navigate(LANGUAGES_DESTINATION) }
            )
        }

        composable(DARK_THEME_DESTINATION) {
            DarkThemeView(
                navigateBack = navigateBack
            )
        }

        composable(LANGUAGES_DESTINATION) {
            LanguagesView(
                navigateBack = navigateBack
            )
        }

        composable(SOURCES_DESTINATION) {
            SourcesView(
                navigateBack = navigateBack,
                navigateToSourcesCatalog = { navController.navigate(CATALOG_DESTINATION) },
                navigateToSourcesManagement = { /*TODO*/ }
            )
        }

        composable(CATALOG_DESTINATION) {
            SourcesCatalogView(
                navigateBack = navigateBack,
            )
        }

        composable(BACKUP_RESTORE_DESTINATION) {
            BackupRestoreView(
                navigateBack = navigateBack,
                navigateToRestoreScreen = {
                    navController.navigate(RESTORE_DESTINATION.replace(RESTORE_ARGUMENT, it))
                }
            )
        }

        composable(
            route = RESTORE_DESTINATION,
            arguments = listOf(
                navArgument(RESTORE_ARGUMENT.removeFirstAndLast()) {
                    type = NavType.StringType
                }
            )
        ) { navEntry ->
            RestoreItemsView(
                uri = navEntry.arguments?.getString(PROVIDER_ARGUMENT.removeFirstAndLast()) ?: "",
                navigateBack = navigateBack
            )
        }

        composable(SHELF_SETTINGS_DESTINATION) {
            ShelfSettingsView(
                navigateBack = navigateBack,
                navigateToCategories = { navController.navigate(CATEGORIES_DESTINATION) }
            )
        }

        composable(CATEGORIES_DESTINATION) {
            CategoriesView(
                navigateBack = navigateBack,
            )
        }

        composable(NETWORK_DESTINATION) {
            NetworkView(
                navigateBack = navigateBack,
            )
        }

        composable(STORAGE_DESTINATION) {
            StorageView(
                navigateBack = navigateBack,
            )
        }

        composable(ADVANCED_DESTINATION) {
            AdvancedView(
                loggers = loggers,
                navigateBack = navigateBack,
                navigateToStats = { navController.navigate(STATS_DESTINATION) }
            )
        }

        composable(STATS_DESTINATION) {
            StatsView(
                navigateBack = navigateBack
            )
        }

        composable(
            route = LIST_DESTINATION,
            arguments = listOf(
                navArgument(PROVIDER_ARGUMENT.removeFirstAndLast()) {
                    type = NavType.StringType
                }
            )
        ) { navEntry ->
            MangaListView(
                coil = coil,
                source = navEntry.arguments?.getString(PROVIDER_ARGUMENT.removeFirstAndLast())
                    ?.let { source -> MangaSource.valueOf(source) } ?: MangaSource.DUMMY,
                navigateBack = navigateBack,
                navigateToDetails = {
                    navController.navigate(
                        DETAILS_DESTINATION.replace(
                            MANGA_ID_ARGUMENT, it.toString()
                        )
                    )
                }
            )
        }

        composable(ABOUT_DESTINATION) {
            AboutView(
                navigateBack = navigateBack,
                navigateToLicensesPage = { navController.navigate(LICENSES_DESTINATION) },
                navigateToUpdatePage = { navController.navigate(UPDATES_DESTINATION) }
            )
        }

        composable(LICENSES_DESTINATION) {
            OpenSourceLicensesView(
                navigateBack = navigateBack,
                navigateToLicensePage = navigateToLicense
            )
        }

        composable(
            route = LICENSE_DESTINATION,
            arguments = listOf(
                navArgument(LICENSE_NAME_ARGUMENT.removeFirstAndLast()) {
                    type = NavType.StringType
                },
                navArgument(LICENSE_WEBSITE_ARGUMENT.removeFirstAndLast()) {
                    type = NavType.StringType
                },
                navArgument(LICENSE_CONTENT_ARGUMENT.removeFirstAndLast()) {
                    type = NavType.StringType
                }
            )
        ) { navEntry ->
            LicenseView(
                name = navEntry.arguments?.getString(LICENSE_NAME_ARGUMENT.removeFirstAndLast())
                    .orEmpty(),
                website = navEntry.arguments?.getString(LICENSE_WEBSITE_ARGUMENT.removeFirstAndLast())
                    .orEmpty(),
                license = navEntry.arguments?.getString(LICENSE_CONTENT_ARGUMENT.removeFirstAndLast())
                    ?: "No license text",
                navigateBack = navigateBack
            )
        }

        composable(UPDATES_DESTINATION) {
            UpdateView(
                navigateBack = navigateBack,
            )
        }

        composable(
            route = DETAILS_DESTINATION,
            arguments = listOf(
                navArgument(MANGA_ID_ARGUMENT.removeFirstAndLast()) {
                    type = NavType.LongType
                }
            ),
        ) { navEntry ->
            DetailsView(
                coil = coil,
                mangaId = navEntry.arguments?.getLong(MANGA_ID_ARGUMENT.removeFirstAndLast()) ?: 0L,
                navigateBack = navigateBack,
                navigateToFullImage = { pictures ->
                    navController.navigate(
                        FULL_POSTER_DESTINATION.replace(PICTURES_ARGUMENT, pictures)
                    )
                },
                navigateToDetails = {
                    navController.navigate(
                        DETAILS_DESTINATION.replace(MANGA_ID_ARGUMENT, it.toString())
                    )
                },
                navigateToSource = {
                    navController.navigate(
                        LIST_DESTINATION.replace(PROVIDER_ARGUMENT, it.name)
                    )
                }
            )
        }

        composable(
            FULL_POSTER_DESTINATION,
            arguments = listOf(
                navArgument(PICTURES_ARGUMENT.removeFirstAndLast()) { type = StringArrayNavType }
            ),
        ) { navEntry ->
            FullImageView(
                coil = coil,
                pictures = navEntry.arguments?.getStringArray(PICTURES_ARGUMENT.removeFirstAndLast())
                    ?: emptyArray(),
                navigateBack = navigateBack
            )
        }
    }
}