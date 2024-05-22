package org.xtimms.shirizu.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet
import okhttp3.OkHttpClient
import org.xtimms.shirizu.core.database.ShirizuDatabase
import org.xtimms.shirizu.core.network.BaseHttpClient
import org.xtimms.shirizu.core.scrobbling.data.ScrobblerStorage
import org.xtimms.shirizu.core.scrobbling.domain.Scrobbler
import org.xtimms.shirizu.core.scrobbling.domain.model.ScrobblerService
import org.xtimms.shirizu.core.scrobbling.domain.model.ScrobblerType
import org.xtimms.shirizu.core.scrobbling.services.kitsu.data.KitsuAuthenticator
import org.xtimms.shirizu.core.scrobbling.services.kitsu.data.KitsuInterceptor
import org.xtimms.shirizu.core.scrobbling.services.kitsu.data.KitsuRepository
import org.xtimms.shirizu.core.scrobbling.services.kitsu.domain.KitsuScrobbler
import org.xtimms.shirizu.core.scrobbling.services.shikimori.data.ShikimoriAuthenticator
import org.xtimms.shirizu.core.scrobbling.services.shikimori.data.ShikimoriInterceptor
import org.xtimms.shirizu.core.scrobbling.services.shikimori.domain.ShikimoriScrobbler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScrobblingModule {

    @Provides
    @Singleton
    @ScrobblerType(ScrobblerService.SHIKIMORI)
    fun provideShikimoriHttpClient(
        @BaseHttpClient baseHttpClient: OkHttpClient,
        authenticator: ShikimoriAuthenticator,
        @ScrobblerType(ScrobblerService.SHIKIMORI) storage: ScrobblerStorage,
    ): OkHttpClient = baseHttpClient.newBuilder().apply {
        authenticator(authenticator)
        addInterceptor(ShikimoriInterceptor(storage))
    }.build()

    @Provides
    @Singleton
    fun provideKitsuRepository(
        @ApplicationContext context: Context,
        @ScrobblerType(ScrobblerService.KITSU) storage: ScrobblerStorage,
        database: ShirizuDatabase,
        authenticator: KitsuAuthenticator,
    ): KitsuRepository {
        val okHttp = OkHttpClient.Builder().apply {
            authenticator(authenticator)
            addInterceptor(KitsuInterceptor(storage))
        }.build()
        return KitsuRepository(context, okHttp, storage, database)
    }

    @Provides
    @Singleton
    @ScrobblerType(ScrobblerService.SHIKIMORI)
    fun provideShikimoriStorage(
        @ApplicationContext context: Context,
    ): ScrobblerStorage = ScrobblerStorage(context, ScrobblerService.SHIKIMORI)

    @Provides
    @Singleton
    @ScrobblerType(ScrobblerService.KITSU)
    fun provideKitsuStorage(
        @ApplicationContext context: Context,
    ): ScrobblerStorage = ScrobblerStorage(context, ScrobblerService.KITSU)

    @Provides
    @ElementsIntoSet
    fun provideScrobblers(
        shikimoriScrobbler: ShikimoriScrobbler,
        kitsuScrobbler: KitsuScrobbler
    ): Set<@JvmSuppressWildcards Scrobbler> = setOf(shikimoriScrobbler, kitsuScrobbler)
}