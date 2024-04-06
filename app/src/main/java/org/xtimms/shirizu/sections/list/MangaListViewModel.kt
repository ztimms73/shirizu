package org.xtimms.shirizu.sections.list

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.shirizu.core.base.viewmodel.BaseViewModel
import org.xtimms.shirizu.core.parser.MangaRepository
import org.xtimms.shirizu.utils.lang.call
import org.xtimms.shirizu.utils.lang.removeFirstAndLast
import org.xtimms.shirizu.utils.lang.require
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class MangaListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    mangaRepositoryFactory: MangaRepository.Factory,
) : BaseViewModel<MangaListUiState>(), MangaListEvent {

    private var loadingJob: Job? = null

    val source = MangaSource.valueOf(savedStateHandle.get<String>(PROVIDER_ARGUMENT.removeFirstAndLast())!!)
    private val repository = mangaRepositoryFactory.create(source)
    private val mangaList = MutableStateFlow<List<Manga>?>(null)
    private val listError = MutableStateFlow<Throwable?>(null)
    private val hasNextPage = MutableStateFlow(false)

    override val mutableUiState = MutableStateFlow(MangaListUiState())

    init {
        setLoading(true)
        launchLoadingJob(Dispatchers.Default) {
            mutableUiState
                .distinctUntilChangedBy { it.loadMore }
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
                    mutableUiState.update {
                        it.copy(
                            manga = list,
                            nextPage = "2",
                            loadMore = hasNextPage.value,
                            isLoading = false
                        )
                    }
                }
        }
    }

    protected fun loadList(append: Boolean): Job {
        loadingJob?.let {
            if (it.isActive) return it
        }
        return launchLoadingJob(Dispatchers.Default) {
            try {
                listError.value = null
                val list = repository.getList(
                    offset = if (append) mangaList.value?.size ?: 0 else 0,
                    filter = null,
                )
                val oldList = mangaList.getAndUpdate { oldList ->
                    if (!append || oldList.isNullOrEmpty()) {
                        list
                    } else {
                        oldList + list
                    }
                }.orEmpty()
                hasNextPage.value = if (append) {
                    list.isNotEmpty()
                } else {
                    list.size > oldList.size || hasNextPage.value
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                listError.value = e
                if (!mangaList.value.isNullOrEmpty()) {
                    errorEvent.call(e)
                }
                hasNextPage.value = false
            }
        }.also { loadingJob = it }
    }

    fun loadNextPage() {
        if (hasNextPage.value && listError.value == null) {
            loadList(append = true)
        }
    }

    override fun loadMore() {
        if (mutableUiState.value.canLoadMore) {
            mutableUiState.update { it.copy(loadMore = true) }
        }
    }

    override fun showMessage(message: String?) {
        TODO("Not yet implemented")
    }

    override fun onMessageDisplayed() {
        TODO("Not yet implemented")
    }

}