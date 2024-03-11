package org.xtimms.tokusho.sections.details.domain

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.tokusho.core.prefs.AppSettings
import org.xtimms.tokusho.data.repository.FavouritesRepository
import org.xtimms.tokusho.data.repository.HistoryRepository
import javax.inject.Inject

class DetailsInteractor @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val favouritesRepository: FavouritesRepository,
) {

    fun observeIsFavourite(mangaId: Long): Flow<Boolean> {
        return favouritesRepository.observeCategoriesIds(mangaId)
            .map { it.isNotEmpty() }
    }

}