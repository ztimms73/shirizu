package org.xtimms.shirizu.core.scrobbling.data

import org.xtimms.shirizu.core.scrobbling.domain.model.ScrobblerManga
import org.xtimms.shirizu.core.scrobbling.domain.model.ScrobblerMangaInfo
import org.xtimms.shirizu.core.scrobbling.domain.model.ScrobblerUser

interface ScrobblerRepository {

    val oauthUrl: String

    val isAuthorized: Boolean

    val cachedUser: ScrobblerUser?

    suspend fun authorize(code: String?)

    suspend fun loadUser(): ScrobblerUser

    fun logout()

    suspend fun unregister(mangaId: Long)

    suspend fun findManga(query: String, offset: Int): List<ScrobblerManga>

    suspend fun getMangaInfo(id: Long): ScrobblerMangaInfo

    suspend fun createRate(mangaId: Long, scrobblerMangaId: Long)

    suspend fun updateRate(rateId: Int, mangaId: Long, chapter: Int)

    suspend fun updateRate(rateId: Int, mangaId: Long, rating: Float, status: String?, comment: String?)
}