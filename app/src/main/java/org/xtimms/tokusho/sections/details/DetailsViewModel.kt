package org.xtimms.tokusho.sections.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.plus
import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.tokusho.core.base.viewmodel.BaseViewModel
import org.xtimms.tokusho.core.parser.MangaIntent
import org.xtimms.tokusho.sections.details.data.MangaDetails
import org.xtimms.tokusho.sections.details.domain.DetailsLoadUseCase
import org.xtimms.tokusho.utils.lang.onEachWhile
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val detailsLoadUseCase: DetailsLoadUseCase,
) : BaseViewModel<DetailsUiState>(), DetailsEvent {

    private val intent = MangaIntent(savedStateHandle)
    val details = MutableStateFlow(intent.manga?.let { MangaDetails(it, null, false) })

    val manga = details.map { x -> x?.toManga() }
        .stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, null)

    override val mutableUiState = MutableStateFlow(DetailsUiState())

    fun getDetails(mangaId: Long) {
        launchLoadingJob(Dispatchers.Default) {
            detailsLoadUseCase.invoke(intent)
                .onEachWhile {
                    if (it.allChapters.isEmpty()) {
                        return@onEachWhile false
                    }
                    true
                }.collect {
                    mutableUiState.update {
                        val manga = details.firstOrNull { it != null } ?: return@collect
                        it.copy(
                            manga = manga.toManga()
                        )
                    }
                }
        }
    }

}