package org.xtimms.shirizu.sections.settings.sources.catalog

import androidx.annotation.MainThread
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.internal.lifecycle.RetainedLifecycleImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import org.koitharu.kotatsu.parsers.model.ContentType
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.util.mapToSet
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.base.viewmodel.KotatsuBaseViewModel
import org.xtimms.shirizu.core.prefs.AppSettings
import org.xtimms.shirizu.data.repository.MangaSourcesRepository
import org.xtimms.shirizu.utils.ReversibleAction
import org.xtimms.shirizu.utils.lang.MutableEventFlow
import org.xtimms.shirizu.utils.lang.call
import java.util.EnumMap
import java.util.EnumSet
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SourcesCatalogViewModel @Inject constructor(
    private val repository: MangaSourcesRepository,
    private val listProducerFactory: SourcesCatalogListProducer.Factory,
) : KotatsuBaseViewModel() {

    private val lifecycle = RetainedLifecycleImpl()
    private var searchQuery: String? = null
    val onActionDone = MutableEventFlow<ReversibleAction>()
    val locales = repository.allMangaSources.mapToSet { it.locale }
    val locale = MutableStateFlow(Locale.getDefault().language.takeIf { it in locales })

    private val listProducers = locale.map { lc ->
        createListProducers(lc)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, createListProducers(locale.value))

    @OptIn(ExperimentalCoroutinesApi::class)
    val content: StateFlow<List<SourceCatalogPage>> = listProducers.flatMapLatest {
        val flows = it.entries.map { (type, producer) -> producer.list.map { x -> SourceCatalogPage(type, x) } }
        combine<SourceCatalogPage, List<SourceCatalogPage>>(flows, Array<SourceCatalogPage>::toList)
    }.stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, emptyList())

    override fun onCleared() {
        super.onCleared()
        lifecycle.dispatchOnCleared()
    }

    fun performSearch(query: String?) {
        searchQuery = query
        listProducers.value.forEach { (_, v) -> v.setQuery(query) }
    }

    fun setLocale(value: String?) {
        locale.value = value
    }

    fun addSource(source: MangaSource) {
        launchJob(Dispatchers.Default) {
            val rollback = repository.setSourceEnabled(source, true)
            onActionDone.call(ReversibleAction(R.string.source_enabled, rollback))
        }
    }

    @MainThread
    private fun createListProducers(lc: String?): Map<ContentType, SourcesCatalogListProducer> {
        val types = EnumSet.allOf(ContentType::class.java)
        if (AppSettings.isNSFWEnabled()) {
            types.remove(ContentType.HENTAI)
        }
        return types.associateWithTo(EnumMap(ContentType::class.java)) { type ->
            listProducerFactory.create(lc, type, lifecycle).also {
                it.setQuery(searchQuery)
            }
        }
    }
}