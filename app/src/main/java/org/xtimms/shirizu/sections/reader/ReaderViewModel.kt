package org.xtimms.shirizu.sections.reader

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import org.xtimms.shirizu.core.base.viewmodel.KotatsuBaseViewModel
import org.xtimms.shirizu.core.parser.MangaDataRepository
import org.xtimms.shirizu.core.parser.MangaIntent
import org.xtimms.shirizu.data.repository.HistoryRepository
import org.xtimms.shirizu.sections.details.data.MangaDetails
import org.xtimms.shirizu.sections.details.domain.DetailsLoadUseCase
import org.xtimms.shirizu.sections.reader.domain.ChaptersLoader
import org.xtimms.shirizu.sections.reader.domain.PageLoader
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataRepository: MangaDataRepository,
    private val historyRepository: HistoryRepository,
    private val detailsLoadUseCase: DetailsLoadUseCase,
    private val pageLoader: PageLoader,
    private val chaptersLoader: ChaptersLoader,
) : KotatsuBaseViewModel() {

    private val intent = MangaIntent(savedStateHandle)

    private var loadingJob: Job? = null
    private var pageSaveJob: Job? = null
    private var bookmarkJob: Job? = null
    private var stateChangeJob: Job? = null

    private val mangaData = MutableStateFlow(intent.manga?.let { MangaDetails(it, null, null, false) })

    val content = MutableStateFlow(ReaderContent(emptyList(), null))
    val manga: MangaDetails?
        get() = mangaData.value

    init {
        loadImpl()
    }

    fun reload() {
        loadingJob?.cancel()
        loadImpl()
    }

    private fun loadImpl() {
        loadingJob = launchLoadingJob(Dispatchers.Default) {

        }
    }

}