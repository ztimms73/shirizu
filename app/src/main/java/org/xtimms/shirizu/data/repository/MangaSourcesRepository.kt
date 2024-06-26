package org.xtimms.shirizu.data.repository

import dagger.Reusable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.koitharu.kotatsu.parsers.model.ContentType
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.util.mapToSet
import org.xtimms.shirizu.core.database.ShirizuDatabase
import org.xtimms.shirizu.core.database.dao.MangaSourcesDao
import org.xtimms.shirizu.core.database.entity.MangaSourceEntity
import org.xtimms.shirizu.core.model.MangaSource
import org.xtimms.shirizu.core.model.isNsfw
import org.xtimms.shirizu.core.prefs.AppSettings
import org.xtimms.shirizu.core.prefs.KotatsuAppSettings
import org.xtimms.shirizu.core.prefs.observeAsFlow
import org.xtimms.shirizu.sections.explore.data.SourcesSortOrder
import org.xtimms.shirizu.utils.ReversibleHandle
import java.util.Collections
import java.util.EnumSet
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@Reusable
class MangaSourcesRepository @Inject constructor(
    private val db: ShirizuDatabase,
    private val settings: KotatsuAppSettings,
) {

    private val dao: MangaSourcesDao
        get() = db.getSourcesDao()

    private val remoteSources = EnumSet.allOf(MangaSource::class.java).apply {
        remove(MangaSource.LOCAL)
        remove(MangaSource.DUMMY)
    }

    val allMangaSources: Set<MangaSource>
        get() = Collections.unmodifiableSet(remoteSources)

    suspend fun getEnabledSources(): List<MangaSource> {
        val order = settings.sourcesSortOrder
        return dao.findAllEnabled(order).toSources(settings.isNsfwContentDisabled)
    }

    suspend fun getDisabledSources(): List<MangaSource> {
        return dao.findAllDisabled().toSources(settings.isNsfwContentDisabled)
    }

    fun observeDisabledSources(): Flow<List<MangaSource>> = combine(
        observeIsNsfwDisabled(),
        observeSortOrder(),
    ) { skipNsfw, _ ->
        dao.observeDisabled().map {
            it.toSources(skipNsfw)
        }
    }.flatMapLatest { it }

    fun observeEnabledSourcesCount(): Flow<Int> {
        return combine(
            observeIsNsfwDisabled(),
            dao.observeEnabled(SourcesSortOrder.MANUAL),
        ) { skipNsfw, sources ->
            sources.count { !skipNsfw || !MangaSource(it.source).isNsfw() }
        }.distinctUntilChanged()
    }

    fun observeAvailableSourcesCount(): Flow<Int> {
        return combine(
            observeIsNsfwDisabled(),
            dao.observeEnabled(SourcesSortOrder.MANUAL),
        ) { skipNsfw, enabledSources ->
            val enabled = enabledSources.mapToSet { it.source }
            allMangaSources.count { x ->
                x.name !in enabled && (!skipNsfw || !x.isNsfw())
            }
        }.distinctUntilChanged()
    }

    fun observeEnabledSources(): Flow<List<MangaSource>> = combine(
        observeIsNsfwDisabled(),
        observeSortOrder(),
    ) { skipNsfw, order ->
        dao.observeEnabled(order).map {
            it.toSources(skipNsfw)
        }
    }.flatMapLatest { it }

    suspend fun setSourceEnabled(source: MangaSource, isEnabled: Boolean): ReversibleHandle {
        dao.setEnabled(source.name, isEnabled)
        return ReversibleHandle {
            dao.setEnabled(source.name, !isEnabled)
        }
    }

    fun observeNewSources(): Flow<Set<MangaSource>> = observeIsNewSourcesEnabled().flatMapLatest {
        if (it) {
            combine(
                dao.observeAll(),
                observeIsNsfwDisabled(),
            ) { entities, skipNsfw ->
                val result = EnumSet.copyOf(remoteSources)
                for (e in entities) {
                    result.remove(MangaSource(e.source))
                }
                if (skipNsfw) {
                    result.removeAll { x -> x.isNsfw() }
                }
                result
            }.distinctUntilChanged()
        } else {
            flowOf(emptySet())
        }
    }

    suspend fun assimilateNewSources(): Set<MangaSource> {
        val new = getNewSources()
        if (new.isEmpty()) {
            return emptySet()
        }
        var maxSortKey = dao.getMaxSortKey()
        val entities = new.map { x ->
            MangaSourceEntity(
                source = x.name,
                isEnabled = false,
                sortKey = ++maxSortKey,
            )
        }
        dao.insertIfAbsent(entities)
        if (settings.isNsfwContentDisabled) {
            new.removeAll { x -> x.isNsfw() }
        }
        return new
    }

    suspend fun isSetupRequired(): Boolean {
        return dao.findAll().isEmpty()
    }

    private suspend fun getNewSources(): MutableSet<MangaSource> {
        val entities = dao.findAll()
        val result = EnumSet.copyOf(remoteSources)
        for (e in entities) {
            result.remove(MangaSource(e.source))
        }
        return result
    }

    private fun List<MangaSourceEntity>.toSources(
        skipNsfwSources: Boolean,
    ): List<MangaSource> {
        val result = ArrayList<MangaSource>(size)
        for (entity in this) {
            val source = MangaSource(entity.source)
            if (skipNsfwSources && source.contentType == ContentType.HENTAI) {
                continue
            }
            if (source in remoteSources) {
                result.add(source)
            }
        }
        return result
    }

    private fun observeIsNsfwDisabled() = MutableStateFlow(AppSettings.isNSFWEnabled()).asStateFlow()

    private fun observeIsNewSourcesEnabled() = settings.observeAsFlow(KotatsuAppSettings.KEY_SOURCES_NEW) {
        isNewSourcesTipEnabled
    }

    private fun observeSortOrder() = settings.observeAsFlow(KotatsuAppSettings.KEY_SOURCES_ORDER) {
        sourcesSortOrder
    }
}