package org.xtimms.shirizu.sections.stats

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.take
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.base.viewmodel.KotatsuBaseViewModel
import org.xtimms.shirizu.core.model.FavouriteCategory
import org.xtimms.shirizu.data.repository.FavouritesRepository
import org.xtimms.shirizu.data.repository.StatsRepository
import org.xtimms.shirizu.sections.stats.domain.StatsPeriod
import org.xtimms.shirizu.sections.stats.domain.StatsRecord
import org.xtimms.shirizu.utils.ReversibleAction
import org.xtimms.shirizu.utils.lang.MutableEventFlow
import org.xtimms.shirizu.utils.lang.call
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: StatsRepository,
    private val favouritesRepository: FavouritesRepository,
) : KotatsuBaseViewModel() {

    val period = MutableStateFlow(StatsPeriod.WEEK)
    val onActionDone = MutableEventFlow<ReversibleAction>()
    val selectedCategories = MutableStateFlow<Set<Long>>(emptySet())
    val favoriteCategories = favouritesRepository.observeCategories()
        .take(1)

    val readingStats = MutableStateFlow<List<StatsRecord>>(emptyList())

    init {
        launchJob(Dispatchers.Default) {
            combine<StatsPeriod, Set<Long>, Pair<StatsPeriod, Set<Long>>>(
                period,
                selectedCategories,
                ::Pair,
            ).collectLatest { p ->
                readingStats.value = withLoading {
                    repository.getReadingStats(p.first, p.second)
                }
            }
        }
    }

    fun setCategoryChecked(category: FavouriteCategory, checked: Boolean) {
        val snapshot = selectedCategories.value.toMutableSet()
        if (checked) {
            snapshot.add(category.id)
        } else {
            snapshot.remove(category.id)
        }
        selectedCategories.value = snapshot
    }

    fun clear() {
        launchLoadingJob(Dispatchers.Default) {
            repository.clearStats()
            readingStats.value = emptyList()
            onActionDone.call(ReversibleAction(R.string.stats_cleared, null))
        }
    }
}