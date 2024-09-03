package org.xtimms.shirizu.sections.list

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaParserSource
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.shirizu.core.parser.MangaRepository

class MangaListScreenModel @AssistedInject constructor(
    @Assisted sourceName: String,
    mangaRepositoryFactory: MangaRepository.Factory,
) : StateScreenModel<MangaListScreenModel.State>(State()) {

    val source = MangaParserSource.valueOf(sourceName)
    private val repository = mangaRepositoryFactory.create(source)
    private val hasNextPage = MutableStateFlow(false)
    private val mangaList = MutableStateFlow<List<Manga>?>(null)

    init {
        screenModelScope.launch(Dispatchers.Default) {
            state.distinctUntilChangedBy { it.loadMore }
                .filter { it.loadMore }
                .collectLatest { uiState ->
                    val list = repository.getList(
                        offset = mangaList.value?.size ?: 0,
                        filter = null,
                    )
                    val oldList = mangaList.getAndUpdate { oldList ->
                        if (oldList.isNullOrEmpty()) {
                            list
                        } else {
                            oldList + list
                        }
                    }.orEmpty()
                    hasNextPage.value = list.size > oldList.size || hasNextPage.value
                    mutableState.update {
                        it.copy(
                            list = list,
                            loadMore = hasNextPage.value,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun getColumnsPreference(orientation: Int): GridCells {
        return GridCells.Fixed(3)
    }

    sealed interface Dialog {
        data object Filter : Dialog
        data class RemoveManga(val manga: Manga) : Dialog
        data class AddDuplicateManga(val manga: Manga, val duplicate: Manga) : Dialog
        data class Migrate(val newManga: Manga, val oldManga: Manga) : Dialog
    }

    @Immutable
    data class State(
        val list: List<Manga> = listOf(),
        val toolbarQuery: String? = null,
        val dialog: Dialog? = null,
        val loadMore: Boolean = true,
        val isLoading: Boolean = false,
    ) {

    }

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(sourceName: String): MangaListScreenModel
    }
}