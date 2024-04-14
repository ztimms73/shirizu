package org.xtimms.shirizu.sections.settings.sources.catalog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.room.InvalidationTracker
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.ViewModelLifecycle
import dagger.hilt.android.lifecycle.RetainedLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koitharu.kotatsu.parsers.model.ContentType
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.database.TABLE_SOURCES
import org.xtimms.shirizu.core.database.ShirizuDatabase
import org.xtimms.shirizu.core.database.removeObserverAsync
import org.xtimms.shirizu.data.repository.MangaSourcesRepository
import org.xtimms.shirizu.utils.lang.lifecycleScope

class SourcesCatalogListProducer @AssistedInject constructor(
    @Assisted private val locale: String?,
    @Assisted private val contentType: ContentType,
    @Assisted lifecycle: ViewModelLifecycle,
    private val repository: MangaSourcesRepository,
    private val database: ShirizuDatabase,
) : InvalidationTracker.Observer(TABLE_SOURCES), RetainedLifecycle.OnClearedListener {

    private val scope = lifecycle.lifecycleScope

    private var query: String? = null
    val list = MutableStateFlow(emptyList<SourceCatalogItemModel>())

    private var job = scope.launch(Dispatchers.Default) {
        list.value = buildList()
    }

    init {
        scope.launch(Dispatchers.Default) {
            database.invalidationTracker.addObserver(this@SourcesCatalogListProducer)
        }
        lifecycle.addOnClearedListener(this)
    }

    override fun onCleared() {
        database.invalidationTracker.removeObserverAsync(this)
    }

    override fun onInvalidated(tables: Set<String>) {
        val prevJob = job
        job = scope.launch(Dispatchers.Default) {
            prevJob.cancelAndJoin()
            list.update { buildList() }
        }
    }

    fun setQuery(value: String?) {
        this.query = value
        onInvalidated(emptySet())
    }

    private suspend fun buildList(): List<SourceCatalogItemModel> {
        val sources = repository.getDisabledSources().toMutableList()
        when (val q = query) {
            null -> sources.retainAll { it.contentType == contentType && it.locale == locale }
            "" -> return emptyList()
            else -> sources.retainAll { it.title.contains(q, ignoreCase = true) }
        }
        sources.sortBy { it.title }
        return sources.map {
            SourceCatalogItemModel(
                source = it,
                showSummary = query != null,
            )
        }
    }

    @AssistedFactory
    interface Factory {

        fun create(
            locale: String?,
            contentType: ContentType,
            lifecycle: ViewModelLifecycle,
        ): SourcesCatalogListProducer
    }
}