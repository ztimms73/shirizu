package org.xtimms.tokusho.sections.shelf

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.plus
import org.xtimms.tokusho.core.base.viewmodel.BaseViewModel
import org.xtimms.tokusho.data.repository.FavouritesRepository
import javax.inject.Inject

@HiltViewModel
class ShelfViewModel @Inject constructor(
    private val favouritesRepository: FavouritesRepository,
) : BaseViewModel<ShelfUiState>() {

    private val categoriesStateFlow = favouritesRepository.observeCategoriesForLibrary()
        .stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, null)

    val isEmpty = categoriesStateFlow.map {
        it?.isEmpty() == true
    }.stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, false)

    init {
        launchJob(Dispatchers.Default) {
            mutableUiState.update {
                it.copy(
                    categories = categoriesStateFlow.value ?: emptyList()
                )
            }
        }
    }

    override val mutableUiState = MutableStateFlow(ShelfUiState())

}