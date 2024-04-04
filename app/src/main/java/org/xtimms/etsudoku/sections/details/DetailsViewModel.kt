package org.xtimms.etsudoku.sections.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
import kotlinx.coroutines.plus
import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.etsudoku.core.base.viewmodel.KotatsuBaseViewModel
import org.xtimms.etsudoku.core.model.findById
import org.xtimms.etsudoku.core.model.getPreferredBranch
import org.xtimms.etsudoku.core.parser.MangaIntent
import org.xtimms.etsudoku.data.repository.BookmarksRepository
import org.xtimms.etsudoku.data.repository.FavouritesRepository
import org.xtimms.etsudoku.data.repository.HistoryRepository
import org.xtimms.etsudoku.sections.details.data.MangaDetails
import org.xtimms.etsudoku.sections.details.domain.BranchComparator
import org.xtimms.etsudoku.sections.details.domain.DetailsInteractor
import org.xtimms.etsudoku.sections.details.domain.DetailsLoadUseCase
import org.xtimms.etsudoku.sections.details.domain.ReadingTimeUseCase
import org.xtimms.etsudoku.sections.details.domain.RelatedMangaUseCase
import org.xtimms.etsudoku.sections.details.model.ChapterItem
import org.xtimms.etsudoku.sections.details.model.HistoryInfo
import org.xtimms.etsudoku.sections.details.model.MangaBranch
import org.xtimms.etsudoku.utils.lang.onEachWhile
import org.xtimms.etsudoku.utils.lang.removeFirstAndLast
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val interactor: DetailsInteractor,
    private val historyRepository: HistoryRepository,
    private val bookmarksRepository: BookmarksRepository,
    private val favouritesRepository: FavouritesRepository,
    private val detailsLoadUseCase: DetailsLoadUseCase,
    private val readingTimeUseCase: ReadingTimeUseCase,
    private val relatedMangaUseCase: RelatedMangaUseCase,
) : KotatsuBaseViewModel() {

    private val _events: Channel<Event> = Channel(Channel.UNLIMITED)
    val events: Flow<Event> = _events.receiveAsFlow()

    private var loadingJob: Job
    private val mangaId = savedStateHandle.get<Long>(MANGA_ID_ARGUMENT.removeFirstAndLast())!!
    private val intent = MangaIntent(savedStateHandle)

    var details = MutableStateFlow(intent.manga?.let { MangaDetails(it, null, null, false) })

    val manga = details.map { x -> x?.toManga() }
        .stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, null)

    val history = historyRepository.observeOne(mangaId)
        .stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, null)

    val favouriteCategories = interactor.observeIsFavourite(mangaId)
        .stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, false)

    val remoteManga = MutableStateFlow<Manga?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val newChaptersCount = details.flatMapLatest { d ->
        flowOf(0)
    }.stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, 0)

    private val chaptersQuery = MutableStateFlow("")
    val selectedBranch = MutableStateFlow<String?>(null)

    val historyInfo: StateFlow<HistoryInfo> = combine(
        manga,
        selectedBranch,
        history,
    ) { m, b, h ->
        HistoryInfo(m, b, h)
    }.stateIn(
        scope = viewModelScope + Dispatchers.Default,
        started = SharingStarted.Eagerly,
        initialValue = HistoryInfo(null, null, null),
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val bookmarks = manga.flatMapLatest {
        if (it != null) bookmarksRepository.observeBookmarks(it) else flowOf(emptyList())
    }.stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Lazily, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val relatedManga: StateFlow<List<Manga>> = manga.mapLatest {
        if (it != null) {
            relatedMangaUseCase.invoke(it).orEmpty()
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val branches: StateFlow<List<MangaBranch>> = combine(
        details,
        selectedBranch,
        history,
    ) { m, b, h ->
        val c = m?.chapters
        if (c.isNullOrEmpty()) {
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
    }.stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, emptyList())

    val isChaptersEmpty: StateFlow<Boolean> = details.map {
        it != null && it.isLoaded && it.allChapters.isEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val chapters = combine(
        combine(
            details,
            history,
            selectedBranch,
            newChaptersCount,
            bookmarks,
        ) { manga, history, branch, news, bookmarks ->
            manga?.mapChapters(
                history,
                news,
                branch,
                bookmarks,
            ).orEmpty()
        },
        chaptersQuery,
    ) { list, query ->
        list.filterSearch(query)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val readingTime = combine(
        details,
        selectedBranch,
        history,
    ) { m, b, h ->
        readingTimeUseCase.invoke(m, b, h)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val selectedBranchValue: String?
        get() = selectedBranch.value

    init {
        loadingJob = doLoad(mangaId)
    }

    fun doLoad(mangaId: Long) = launchLoadingJob(Dispatchers.Default) {
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
            }.collect {
                details.value = it
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
}