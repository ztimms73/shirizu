package org.xtimms.shirizu.sections.shelf

import androidx.compose.runtime.Immutable
import androidx.compose.ui.util.fastDistinctBy
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.shirizu.core.model.FavouriteCategory
import org.xtimms.shirizu.core.model.ListSortOrder
import org.xtimms.shirizu.core.model.isLocal
import org.xtimms.shirizu.data.repository.FavouritesRepository
import javax.inject.Inject

/**
 * Typealias for the library manga, using the category as keys, and list of manga as values.
 */
typealias ShelfMap = Map<FavouriteCategory, List<ShelfItem>>

class ShelfScreenModel @Inject constructor(
    private val favouritesRepository: FavouritesRepository,
) : StateScreenModel<ShelfScreenModel.State>(State()) {

    var activeCategoryIndex: Int = 0

    init {
        screenModelScope.launch(Dispatchers.IO) {
            getShelfFlow().collectLatest {
                mutableState.update { state ->
                    state.copy(
                        isLoading = false,
                        shelf = it
                    )
                }
            }
        }
    }

    private fun getShelfFlow(): Flow<ShelfMap> {
        val shelfMangasFlow = favouritesRepository.observeAllShelfManga(ListSortOrder.NEWEST)
            .map { shelfManga -> shelfManga.map {
                ShelfItem(
                    it,
                    isLocal = it.manga.isLocal,
                )
            }.groupBy { it.shelfManga.category }
        }
        return combine(favouritesRepository.observeCategories(), shelfMangasFlow) { categories, mangas ->
            categories.associateWith { mangas[it.id].orEmpty() }
        }
    }

    sealed interface Dialog {
        data object SettingsSheet : Dialog
        data object ChangeCategory : Dialog
        data class DeleteManga(val manga: List<Manga>) : Dialog
    }

    @Immutable
    private data class ItemPreferences(
        val downloadBadge: Boolean,
        val localBadge: Boolean,
        val languageBadge: Boolean,
    )

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val shelf: ShelfMap = emptyMap(),
        val searchQuery: String? = null,
        val selection: PersistentList<ShelfManga> = persistentListOf(),
        val hasActiveFilters: Boolean = false,
        val showMangaCount: Boolean = false,
        val showMangaContinueButton: Boolean = false,
        val dialog: Dialog? = null,
    ) {

        private val shelfCount by lazy {
            shelf.values
                .flatten()
                .fastDistinctBy { it.shelfManga.manga.id }
                .size
        }

        val isShelfEmpty by lazy { shelfCount == 0 }

        val selectionMode = selection.isNotEmpty()

        val categories = shelf.keys.toList()

        fun getShelfItemsByCategoryId(categoryId: Long): List<ShelfItem>? {
            return shelf.firstNotNullOfOrNull { (k, v) -> v.takeIf { k.id == categoryId } }
        }

        fun getShelfItemsByPage(page: Int): List<ShelfItem> {
            return shelf.values.toTypedArray().getOrNull(page).orEmpty()
        }

        fun getMangaCountForCategory(category: FavouriteCategory): Int? {
            return if (showMangaCount || !searchQuery.isNullOrEmpty()) shelf[category]?.size else null
        }
    }
}