package org.xtimms.shirizu.data

import androidx.collection.MutableLongObjectMap
import org.koitharu.kotatsu.parsers.util.runCatchingCancellable
import org.xtimms.shirizu.core.model.LocalManga
import org.xtimms.shirizu.core.parser.local.input.LocalMangaInput
import java.io.File

class LocalMangaMappingCache {

    private val map = MutableLongObjectMap<File>()

    suspend fun get(mangaId: Long): LocalManga? {
        val file = synchronized(this) {
            map[mangaId]
        } ?: return null
        return runCatchingCancellable {
            LocalMangaInput.of(file).getManga()
        }.onFailure {
            it.printStackTrace()
        }.getOrNull()
    }

    operator fun set(mangaId: Long, localManga: LocalManga?) = synchronized(this) {
        if (localManga == null) {
            map.remove(mangaId)
        } else {
            map[mangaId] = localManga.file
        }
    }
}