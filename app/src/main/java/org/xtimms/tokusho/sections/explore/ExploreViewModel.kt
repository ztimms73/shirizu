package org.xtimms.tokusho.sections.explore

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.xtimms.tokusho.core.base.viewmodel.BaseViewModel
import org.xtimms.tokusho.data.repository.MangaSourcesRepository
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val mangaSourcesRepository: MangaSourcesRepository,
) : BaseViewModel<ExploreUiState>(), ExploreEvent {

    override val mutableUiState = MutableStateFlow(
        ExploreUiState(
            isLoading = true,
        )
    )

    init {
        launchJob(Dispatchers.Default) {
            val result = mangaSourcesRepository.allMangaSources
            mutableUiState.update {
                it.copy(
                    sources = result.toList(),
                )
            }
            setLoading(false)
        }
    }

}