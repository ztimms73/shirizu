package org.xtimms.tokusho.sections.shelf

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.xtimms.tokusho.core.base.viewmodel.BaseViewModel
import org.xtimms.tokusho.core.base.viewmodel.KotatsuBaseViewModel
import org.xtimms.tokusho.data.repository.FavouritesRepository
import org.xtimms.tokusho.utils.lang.mapItems
import javax.inject.Inject

@HiltViewModel
class ShelfViewModel @Inject constructor(
    favouritesRepository: FavouritesRepository,
) : KotatsuBaseViewModel() {

    private val mangasStateFlow = favouritesRepository.observeAll(1)
        .stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, null)


    private val categoriesStateFlow = favouritesRepository.observeCategoriesForLibrary()
        .stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, null)

    val mangaCount = favouritesRepository.observeMangaCount()
        .stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, null)

    val categories = categoriesStateFlow
        .filterNotNull()
        .mapItems { FavouriteTabModel(it.id, it.title, mangaCount.value ?: 0) }
        .distinctUntilChanged()
        .stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, emptyList())

    val mangas = mangasStateFlow
        .filterNotNull()
        .mapItems { ShelfManga(it) }
        .distinctUntilChanged()
        .stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, emptyList())

    val isEmpty = categoriesStateFlow.map {
        it?.isEmpty() == true
    }.stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, false)

}