package org.xtimms.shirizu.sections.details

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.plus
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.shirizu.core.base.viewmodel.BaseStateScreenModel
import org.xtimms.shirizu.core.model.findById
import org.xtimms.shirizu.core.model.getPreferredBranch
import org.xtimms.shirizu.data.repository.BookmarksRepository
import org.xtimms.shirizu.data.repository.FavouritesRepository
import org.xtimms.shirizu.data.repository.HistoryRepository
import org.xtimms.shirizu.sections.details.data.MangaDetails
import org.xtimms.shirizu.sections.details.data.ReadingTime
import org.xtimms.shirizu.sections.details.domain.BranchComparator
import org.xtimms.shirizu.sections.details.domain.DetailsInteractor
import org.xtimms.shirizu.sections.details.domain.DetailsLoadUseCase
import org.xtimms.shirizu.sections.details.domain.ReadingTimeUseCase
import org.xtimms.shirizu.sections.details.domain.RelatedMangaUseCase
import org.xtimms.shirizu.sections.details.model.ChapterItem
import org.xtimms.shirizu.sections.details.model.HistoryInfo
import org.xtimms.shirizu.sections.details.model.MangaBranch
import org.xtimms.shirizu.utils.lang.onEachWhile

class DetailsScreenModel @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val manga: Manga,
    private val interactor: DetailsInteractor,
    private val historyRepository: HistoryRepository,
    private val bookmarksRepository: BookmarksRepository,
    private val favouritesRepository: FavouritesRepository,
    private val detailsLoadUseCase: DetailsLoadUseCase,
    private val readingTimeUseCase: ReadingTimeUseCase,
    private val relatedMangaUseCase: RelatedMangaUseCase,
    @Assisted val snackbarHostState: SnackbarHostState = SnackbarHostState(),
) : BaseStateScreenModel<DetailsScreenModel.State>(State.Loading) {

    private val successState: State.Success?
        get() = state.value as? State.Success

    private val _events: Channel<Event> = Channel(Channel.UNLIMITED)
    val events: Flow<Event> = _events.receiveAsFlow()

    private inline fun updateSuccessState(func: (State.Success) -> State.Success) {
        mutableState.update {
            when (it) {
                State.Loading -> it
                is State.Success -> func(it)
            }
        }
    }

    private var loadingJob: Job

    var details = MutableStateFlow(MangaDetails(manga, null, null, false))

    private val mangaImpl = details.map { x -> x.toManga() }
        .stateIn(screenModelScope + Dispatchers.Default, SharingStarted.Eagerly, null)

    val history = historyRepository.observeOne(manga.id)
        .stateIn(screenModelScope + Dispatchers.Default, SharingStarted.Eagerly, null)

    val favouriteCategories = interactor.observeIsFavourite(manga.id)
        .stateIn(screenModelScope + Dispatchers.Default, SharingStarted.Eagerly, false)

    val remoteManga = MutableStateFlow<Manga?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val newChaptersCount = details.flatMapLatest { d ->
        flowOf(0)
    }.stateIn(screenModelScope + Dispatchers.Default, SharingStarted.Eagerly, 0)

    private val chaptersQuery = MutableStateFlow("")
    val selectedBranch = MutableStateFlow<String?>(null)

    val historyInfo: StateFlow<HistoryInfo> = combine(
        mangaImpl,
        selectedBranch,
        history,
    ) { m, b, h ->
        HistoryInfo(m, b, h)
    }.stateIn(
        scope = screenModelScope + Dispatchers.Default,
        started = SharingStarted.Eagerly,
        initialValue = HistoryInfo(null, null, null),
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val bookmarks = mangaImpl.flatMapLatest {
        if (it != null) bookmarksRepository.observeBookmarks(it) else flowOf(emptyList())
    }.stateIn(screenModelScope + Dispatchers.Default, SharingStarted.Lazily, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val relatedManga: StateFlow<List<Manga>> = mangaImpl.mapLatest {
        if (it != null) {
            relatedMangaUseCase.invoke(it).orEmpty()
        } else {
            emptyList()
        }
    }.stateIn(screenModelScope, SharingStarted.Lazily, emptyList())

    val branches: StateFlow<List<MangaBranch>> = combine(
        details,
        selectedBranch,
        history,
    ) { m, b, h ->
        val c = m.chapters
        if (c.isEmpty()) {
            return@combine emptyList()
        }
        val currentBranch = h?.let { m.allChapters.findById(it.chapterId) }?.branch
        c.map { x ->
            MangaBranch(
                name = x.key,
                count = x.value.size,
                isSelected = x.key == b,
                isCurrent = h != null && x.key == currentBranch,
            )
        }.sortedWith(BranchComparator())
    }.stateIn(screenModelScope + Dispatchers.Default, SharingStarted.Eagerly, emptyList())

    val isChaptersEmpty: StateFlow<Boolean> = details.map {
        it.isLoaded && it.allChapters.isEmpty()
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(), false)

    val chapters = combine(
        combine(
            details,
            history,
            selectedBranch,
            newChaptersCount,
            bookmarks,
        ) { manga, history, branch, news, bookmarks ->
            manga.mapChapters(
                history,
                news,
                branch,
                bookmarks,
            )
        },
        chaptersQuery,
    ) { list, query ->
        list.filterSearch(query)
    }.stateIn(screenModelScope, SharingStarted.Eagerly, emptyList())

    val readingTime = combine(
        details,
        selectedBranch,
        history,
    ) { m, b, h ->
        readingTimeUseCase.invoke(m, b, h)
    }.stateIn(screenModelScope, SharingStarted.Lazily, null)

    val selectedBranchValue: String?
        get() = selectedBranch.value

    init {
        loadingJob = doLoad(manga.id)
        updateSuccessState { it.copy(isRefreshingData = false) }
    }

    private fun doLoad(mangaId: Long) = launchLoadingJob(Dispatchers.Default) {
        detailsLoadUseCase.invoke(mangaId)
            .onEachWhile {
                if (it.allChapters.isEmpty()) {
                    return@onEachWhile false
                }
                val manga = it.toManga()
                // find default branch
                val hist = historyRepository.getOne(manga)
                selectedBranch.value = manga.getPreferredBranch(hist)
                true
            }.catch { error ->
                _events.send(Event.InternalError)
                snackbarHostState.showSnackbar(error.message ?: error.stackTraceToString())
            }.collect {
                details.value = it
                mutableState.update {
                    State.Success(
                        manga = details.value.toManga(),
                        source = details.value.toManga().source,
                        readingTime = checkNotNull(readingTime.value),
                        historyInfo = historyInfo.value,
                        availableScanlators = setOf(),
                        excludedScanlators = setOf(),
                        isRefreshingData = false
                    )
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

    fun removeFromHistory() {
        launchJob(Dispatchers.Default) {
            historyRepository.delete(setOf(manga.id))
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
            val manga: Manga,
            val source: MangaSource,
            val historyInfo: HistoryInfo,
            val readingTime: ReadingTime,
            val availableScanlators: Set<String>,
            val excludedScanlators: Set<String>,
            val isRefreshingData: Boolean = false,
        ) : State
    }

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(
            context: Context,
            manga: Manga,
            snackbarHostState: SnackbarHostState
        ): DetailsScreenModel
    }
}