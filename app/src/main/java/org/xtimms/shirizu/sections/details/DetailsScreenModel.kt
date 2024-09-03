package org.xtimms.shirizu.sections.details

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import org.xtimms.shirizu.core.parser.MangaDataRepository
import org.xtimms.shirizu.data.repository.BookmarksRepository
import org.xtimms.shirizu.data.repository.FavouritesRepository
import org.xtimms.shirizu.data.repository.HistoryRepository
import org.xtimms.shirizu.sections.details.data.MangaDetails
import org.xtimms.shirizu.sections.details.domain.DetailsInteractor
import org.xtimms.shirizu.sections.details.domain.DetailsLoadUseCase
import org.xtimms.shirizu.sections.details.domain.ReadingTimeUseCase
import org.xtimms.shirizu.sections.details.domain.RelatedMangaUseCase
import org.xtimms.shirizu.sections.details.model.ChapterItem
import org.xtimms.shirizu.utils.system.getDisplayMessage

class DetailsScreenModel @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val mangaId: Long,
    private val interactor: DetailsInteractor,
    private val mangaDataRepository: MangaDataRepository,
    private val historyRepository: HistoryRepository,
    private val bookmarksRepository: BookmarksRepository,
    private val favouritesRepository: FavouritesRepository,
    private val detailsLoadUseCase: DetailsLoadUseCase,
    private val readingTimeUseCase: ReadingTimeUseCase,
    private val relatedMangaUseCase: RelatedMangaUseCase,
    @Assisted val snackbarHostState: SnackbarHostState = SnackbarHostState(),
) : StateScreenModel<DetailsScreenModel.State>(State.Loading) {

    private val successState: State.Success?
        get() = state.value as? State.Success

    val details: MangaDetails?
        get() = successState?.details

    val history = historyRepository.observeOne(mangaId)
        .stateIn(screenModelScope + Dispatchers.Default, SharingStarted.Eagerly, null)

    private inline fun updateSuccessState(func: (State.Success) -> State.Success) {
        mutableState.update {
            when (it) {
                State.Loading -> it
                is State.Success -> func(it)
            }
        }
    }

    private val selectedPositions: Array<Int> = arrayOf(-1, -1) // first and last selected index in list
    private val selectedChapterIds: HashSet<Long> = HashSet()

    init {
        screenModelScope.launch(Dispatchers.IO) {
            detailsLoadUseCase.invoke(mangaId)
                .collectLatest { details ->
                    updateSuccessState {
                        it.copy(
                            details = details
                        )
                    }
                }
        }

        screenModelScope.launch(Dispatchers.IO) {
            val manga = requireNotNull(mangaDataRepository.findMangaById(mangaId))
            val details = MangaDetails(manga, null, null, false)

            val needRefreshInfo = !details.isLoaded

            mutableState.update {
                State.Success(
                    details = details
                )
            }

            if (screenModelScope.isActive) {
                val fetchFromSourceTasks = listOf(
                    async { if (needRefreshInfo) fetchMangaFromSource() },
                )
                fetchFromSourceTasks.awaitAll()
            }

            updateSuccessState { it.copy(isRefreshingData = false) }
        }
    }

    private suspend fun fetchMangaFromSource(manualFetch: Boolean = false) {
        val state = successState ?: return
        try {
            withContext(Dispatchers.IO) {
                val networkManga = state.details.toManga()
                detailsLoadUseCase.getDetails(networkManga)
            }
        } catch (e: Throwable) {
            screenModelScope.launch {
                snackbarHostState.showSnackbar(message = with(context) { e.getDisplayMessage(resources) })
            }
        }
    }

    private fun List<ChapterItem>.filterSearch(query: String): List<ChapterItem> {
        if (query.isEmpty() || this.isEmpty()) {
            return this
        }
        return filter {
            it.chapter.name.contains(query, ignoreCase = true)
        }
    }

    sealed interface Event {
        data object InternalError : Event
    }

    sealed interface State {
        @Immutable
        data object Loading : State

        @Immutable
        data class Success(
            val details: MangaDetails,
            val isRefreshingData: Boolean = false,
        ) : State
    }

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(
            context: Context,
            mangaId: Long,
            snackbarHostState: SnackbarHostState
        ): DetailsScreenModel
    }
}