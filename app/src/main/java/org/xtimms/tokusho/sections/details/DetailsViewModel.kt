package org.xtimms.tokusho.sections.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.plus
import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.tokusho.core.base.viewmodel.BaseViewModel
import org.xtimms.tokusho.core.parser.MangaIntent
import org.xtimms.tokusho.data.repository.HistoryRepository
import org.xtimms.tokusho.sections.details.data.MangaDetails
import org.xtimms.tokusho.sections.details.domain.DetailsLoadUseCase
import org.xtimms.tokusho.sections.details.domain.ReadingTimeUseCase
import org.xtimms.tokusho.utils.lang.onEachWhile
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val historyRepository: HistoryRepository,
    private val detailsLoadUseCase: DetailsLoadUseCase,
    private val readingTimeUseCase: ReadingTimeUseCase,
) : BaseViewModel<DetailsUiState>(), DetailsEvent {

    override val mutableUiState = MutableStateFlow(DetailsUiState())

    private val intent = MangaIntent(savedStateHandle)
    private val mangaId = intent.id

    private var loadingJob: Job

    val details = MutableStateFlow(intent.manga?.let { MangaDetails(it, null, false) })
    val mangaD = details.map { x -> x?.toManga() }
        .stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, null)

    val history = historyRepository.observeOne(mangaId)
        .stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, null)

    val remoteManga = MutableStateFlow<Manga?>(null)

    private val chaptersQuery = MutableStateFlow("")
    val selectedBranch = MutableStateFlow<String?>(null)

    @Deprecated("")
    val description = details
        .map { it?.description }
        .stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Lazily, null)

    val isChaptersEmpty: StateFlow<Boolean> = details.map {
        it != null && it.isLoaded && it.allChapters.isEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val readingTime = combine(
        details,
        selectedBranch,
        history,
    ) { m, b, h ->
        readingTimeUseCase.invoke(m, b, h)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        loadingJob = doLoad()
    }

    fun reload() {
        loadingJob.cancel()
        loadingJob = doLoad()
    }

    private fun doLoad() = launchLoadingJob(Dispatchers.Default) {
        detailsLoadUseCase.invoke(mangaId ?: 0L)
            .onEachWhile {
                if (it.allChapters.isEmpty()) {
                    return@onEachWhile false
                }
                true
            }.collect {
                //details.value = it
                mutableUiState.update {
                    it.copy(
                        details = details.value
                    )
                }
            }
    }
}