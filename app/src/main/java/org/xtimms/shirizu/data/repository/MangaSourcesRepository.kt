package org.xtimms.shirizu.data.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import org.koitharu.kotatsu.parsers.model.ContentType
import org.koitharu.kotatsu.parsers.model.MangaParserSource
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.util.mapToSet
import org.xtimms.shirizu.BuildConfig
import org.xtimms.shirizu.core.database.ShirizuDatabase
import org.xtimms.shirizu.core.database.dao.MangaSourcesDao
import org.xtimms.shirizu.core.database.entity.MangaSourceEntity
import org.xtimms.shirizu.core.model.MangaSource
import org.xtimms.shirizu.core.model.MangaSourceInfo
import org.xtimms.shirizu.core.model.getTitle
import org.xtimms.shirizu.core.model.isNsfw
import org.xtimms.shirizu.core.parser.external.ExternalMangaSource
import org.xtimms.shirizu.core.prefs.AppSettings
import org.xtimms.shirizu.core.prefs.KotatsuAppSettings
import org.xtimms.shirizu.core.prefs.observeAsFlow
import org.xtimms.shirizu.sections.explore.data.SourcesSortOrder
import org.xtimms.shirizu.utils.ReversibleHandle
import java.util.Collections
import java.util.EnumSet
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaSourcesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: ShirizuDatabase,
    private val settings: KotatsuAppSettings,
) {

    private val isNewSourcesAssimilated = AtomicBoolean(false)
    private val dao: MangaSourcesDao
        get() = db.getSourcesDao()

    private val remoteSources = EnumSet.allOf(MangaParserSource::class.java).apply {
        if (!BuildConfig.DEBUG) {
            remove(MangaParserSource.DUMMY)
        }
    }

    val allMangaSources: Set<MangaParserSource>
        get() = Collections.unmodifiableSet(remoteSources)

    suspend fun getEnabledSources(): List<MangaSource> {
        val order = settings.sourcesSortOrder
        return dao.findAllEnabled(order).toSources(settings.isNsfwContentDisabled)
    }

    suspend fun getDisabledSources(): List<MangaSource> {
        return dao.findAllDisabled().toSources(settings.isNsfwContentDisabled)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeDisabledSources(): Flow<List<MangaParserSource>> = combine(
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

    fun observeEnabledSources(): Flow<List<MangaParserSource>> = combine(
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

    suspend fun assimilateNewSources(): Boolean {
        if (isNewSourcesAssimilated.getAndSet(true)) {
            return false
        }
        val new = getNewSources()
        if (new.isEmpty()) {
            return false
        }
        var maxSortKey = dao.getMaxSortKey()
        val entities = new.map { x ->
            MangaSourceEntity(
                source = x.name,
                isEnabled = false,
                sortKey = ++maxSortKey,
                addedIn = BuildConfig.VERSION_CODE,
                lastUsedAt = 0,
                isPinned = false,
            )
        }
        dao.insertIfAbsent(entities)
        return true
    }

    suspend fun isSetupRequired(): Boolean {
        return dao.findAll().isEmpty()
    }

    private suspend fun getNewSources(): MutableSet<out MangaSource> {
        val entities = dao.findAll()
        val result = EnumSet.copyOf(remoteSources)
        for (e in entities) {
            result.remove(MangaSource(e.source))
        }
        return result
    }

    private fun List<MangaSourceEntity>.toSources(
        skipNsfwSources: Boolean,
    ): List<MangaParserSource> {
        val result = ArrayList<MangaParserSource>(size)
        for (entity in this) {
            val source = entity.source.toMangaSourceOrNull() ?: continue
            if (skipNsfwSources && source.isNsfw()) {
                continue
            }
            if (source in remoteSources) {
                result.add(source)
            }
        }
        return result
    }

    private fun observeExternalSources(): Flow<List<ExternalMangaSource>> {
        val intent = Intent("app.kotatsu.parser.PROVIDE_MANGA")
        val pm = context.packageManager
        return callbackFlow {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    trySendBlocking(intent)
                }
            }
            ContextCompat.registerReceiver(
                context,
                receiver,
                IntentFilter().apply {
                    addAction(Intent.ACTION_PACKAGE_ADDED)
                    addAction(Intent.ACTION_PACKAGE_VERIFIED)
                    addAction(Intent.ACTION_PACKAGE_REPLACED)
                    addAction(Intent.ACTION_PACKAGE_REMOVED)
                    addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
                    addDataScheme("package")
                },
                ContextCompat.RECEIVER_EXPORTED,
            )
            awaitClose { context.unregisterReceiver(receiver) }
        }.onStart {
            emit(null)
        }.map {
            pm.queryIntentContentProviders(intent, 0).map { resolveInfo ->
                ExternalMangaSource(
                    packageName = resolveInfo.providerInfo.packageName,
                    authority = resolveInfo.providerInfo.authority,
                )
            }
        }.distinctUntilChanged()
    }

    private fun observeIsNsfwDisabled() = MutableStateFlow(AppSettings.isNSFWEnabled()).asStateFlow()

    private fun observeIsNewSourcesEnabled() = settings.observeAsFlow(KotatsuAppSettings.KEY_SOURCES_NEW) {
        isNewSourcesTipEnabled
    }

    private fun observeSortOrder() = settings.observeAsFlow(KotatsuAppSettings.KEY_SOURCES_ORDER) {
        sourcesSortOrder
    }

    private fun String.toMangaSourceOrNull(): MangaParserSource? = MangaParserSource.entries.find { it.name == this }
}