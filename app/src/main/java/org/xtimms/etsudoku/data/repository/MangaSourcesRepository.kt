package org.xtimms.etsudoku.data.repository

import androidx.compose.runtime.Composable
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
import org.xtimms.etsudoku.BuildConfig
import org.xtimms.etsudoku.core.database.EtsudokuDatabase
import org.xtimms.etsudoku.core.database.dao.MangaSourcesDao
import org.xtimms.etsudoku.core.database.entity.MangaSourceEntity
import org.xtimms.etsudoku.core.model.MangaSource
import org.xtimms.etsudoku.core.model.isNsfw
import org.xtimms.etsudoku.core.prefs.AppSettings
import org.xtimms.etsudoku.core.prefs.KotatsuAppSettings
import org.xtimms.etsudoku.core.prefs.observeAsFlow
import org.xtimms.etsudoku.sections.explore.data.SourcesSortOrder
import org.xtimms.etsudoku.utils.ReversibleHandle
import java.util.Collections
import java.util.EnumSet
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@Reusable
class MangaSourcesRepository @Inject constructor(
    private val db: EtsudokuDatabase,
    private val settings: KotatsuAppSettings,
) {

    private val dao: MangaSourcesDao
        get() = db.getSourcesDao()

    private val remoteSources = EnumSet.allOf(MangaSource::class.java).apply {
        remove(MangaSource.LOCAL)
        if (!BuildConfig.DEBUG) {
            remove(MangaSource.DUMMY)
        }
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