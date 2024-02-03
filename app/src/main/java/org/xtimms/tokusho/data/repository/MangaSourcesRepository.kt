package org.xtimms.tokusho.data.repository

import dagger.Reusable
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.tokusho.BuildConfig
import org.xtimms.tokusho.core.database.MangaDatabase
import org.xtimms.tokusho.core.database.dao.MangaSourcesDao
import java.util.Collections
import java.util.EnumSet
import javax.inject.Inject

@Reusable
class MangaSourcesRepository @Inject constructor(
    private val db: MangaDatabase,
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

}