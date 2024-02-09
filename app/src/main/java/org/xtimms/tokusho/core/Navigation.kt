package org.xtimms.tokusho.core

import android.graphics.Path
import android.view.animation.PathInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
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
import org.xtimms.tokusho.core.model.ShelfCategory
import org.xtimms.tokusho.core.motion.materialSharedAxisXIn
import org.xtimms.tokusho.core.motion.materialSharedAxisXOut
import org.xtimms.tokusho.sections.details.DETAILS_DESTINATION
import org.xtimms.tokusho.sections.details.DetailsView
import org.xtimms.tokusho.sections.details.MANGA_ID_ARGUMENT
import org.xtimms.tokusho.sections.explore.ExploreView
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
import org.xtimms.tokusho.sections.shelf.ShelfMap
import org.xtimms.tokusho.sections.shelf.ShelfView
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
    navController: NavHostController,
    isCompactScreen: Boolean,
    modifier: Modifier,
    padding: PaddingValues,
    topBarHeightPx: Float,
    topBarOffsetY: Animatable<Float, AnimationVector1D>,
    listState: LazyGridState,
) {

    val navigateBack: () -> Unit = { navController.popBackStack() }

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
            val library: ShelfMap = emptyMap()
            ShelfView(
                categories = listOf(
                    ShelfCategory(1, "Test 1", 1L, 1L),
                    ShelfCategory(2, "Test 2", 2L, 2L)
                ),
                currentPage = { 0 },
                showPageTabs = true,
                getNumberOfMangaForCategory = { 2 },
                getLibraryForPage = { library.values.toTypedArray().getOrNull(0).orEmpty() },
                padding = padding,
                topBarHeightPx = topBarHeightPx,
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
                topBarOffsetY = topBarOffsetY,
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

        composable(SETTINGS_DESTINATION) {
            SettingsView(
                navigateBack = navigateBack,
                navigateToAppearance = { navController.navigate(APPEARANCE_DESTINATION) },
                navigateToAbout = { navController.navigate(ABOUT_DESTINATION) },
                navigateToAdvanced = { navController.navigate(ADVANCED_DESTINATION) }
            )
        }

        composable(APPEARANCE_DESTINATION) {
            AppearanceView(
                coil = coil,
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

        composable(ADVANCED_DESTINATION) {
            AdvancedView(
                navigateBack = navigateBack,
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
                navigateToUpdatePage = { navController.navigate(UPDATES_DESTINATION) }
            )
        }

        composable(UPDATES_DESTINATION) {
            UpdateView(
                navigateBack = navigateBack,
            )
        }

        // TODO
        composable(
            route = DETAILS_DESTINATION
        ) { navEntry ->
            DetailsView(
                coil = coil,
                mangaId = 0L,
                navigateBack = navigateBack,
            )
        }
    }

}