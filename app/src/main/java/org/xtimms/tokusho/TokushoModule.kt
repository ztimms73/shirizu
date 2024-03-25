package org.xtimms.tokusho

import android.app.Application
import android.content.Context
import android.text.Html
import androidx.work.WorkManager
import coil.ComponentRegistry
import coil.ImageLoader
import coil.disk.DiskCache
import coil.util.DebugLogger
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.OkHttpClient
import org.koitharu.kotatsu.parsers.MangaLoaderContext
import org.xtimms.tokusho.core.cache.CacheDir
import org.xtimms.tokusho.core.cache.ContentCache
import org.xtimms.tokusho.core.cache.MemoryContentCache
import org.xtimms.tokusho.core.cache.StubContentCache
import org.xtimms.tokusho.core.database.TokushoDatabase
import org.xtimms.tokusho.core.model.LocalManga
import org.xtimms.tokusho.core.network.MangaHttpClient
import org.xtimms.tokusho.core.os.NetworkState
import org.xtimms.tokusho.core.parser.MangaLoaderContextImpl
import org.xtimms.tokusho.core.parser.MangaRepository
import org.xtimms.tokusho.core.parser.favicon.FaviconFetcher
import org.xtimms.tokusho.core.parser.local.LocalStorageChanges
import org.xtimms.tokusho.utils.CoilImageGetter
import org.xtimms.tokusho.utils.system.connectivityManager
import org.xtimms.tokusho.utils.system.isLowRamDevice
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface TokushoModule {

    @Binds
    fun bindMangaLoaderContext(mangaLoaderContextImpl: MangaLoaderContextImpl): MangaLoaderContext

    @Binds
    fun bindImageGetter(coilImageGetter: CoilImageGetter): Html.ImageGetter

    companion object {

        @Provides
        @Singleton
        fun provideNetworkState(
            @ApplicationContext context: Context
        ) = NetworkState(context.connectivityManager)

        @Provides
        @Singleton
        fun provideMangaDatabase(
            @ApplicationContext context: Context,
        ): TokushoDatabase {
            return TokushoDatabase(context)
        }

        @Provides
        @Singleton
        fun provideCoil(
            @ApplicationContext context: Context,
            @MangaHttpClient okHttpClient: OkHttpClient,
            mangaRepositoryFactory: MangaRepository.Factory,
        ): ImageLoader {
            val diskCacheFactory = {
                val rootDir = context.externalCacheDir ?: context.cacheDir
                DiskCache.Builder()
                    .directory(rootDir.resolve(CacheDir.THUMBS.dir))
                    .build()
            }
            return ImageLoader.Builder(context)
                .crossfade(500)
                .okHttpClient(okHttpClient.newBuilder().cache(null).build())
                .interceptorDispatcher(Dispatchers.Default)
                .fetcherDispatcher(Dispatchers.IO)
                .decoderDispatcher(Dispatchers.Default)
                .transformationDispatcher(Dispatchers.Default)
                .diskCache(diskCacheFactory)
                .logger(if (BuildConfig.DEBUG) DebugLogger() else null)
                .components(
                    ComponentRegistry.Builder()
                        .add(FaviconFetcher.Factory(context, okHttpClient, mangaRepositoryFactory))
                        .build(),
                ).build()
        }

        @Provides
        @Singleton
        fun provideContentCache(
            application: Application,
        ): ContentCache {
            return if (application.isLowRamDevice()) {
                StubContentCache()
            } else {
                MemoryContentCache(application)
            }
        }

        @Provides
        @Singleton
        @LocalStorageChanges
        fun provideMutableLocalStorageChangesFlow(): MutableSharedFlow<LocalManga?> = MutableSharedFlow()

        @Provides
        @LocalStorageChanges
        fun provideLocalStorageChangesFlow(
            @LocalStorageChanges flow: MutableSharedFlow<LocalManga?>,
        ): SharedFlow<LocalManga?> = flow.asSharedFlow()

        @Provides
        fun provideWorkManager(
            @ApplicationContext context: Context,
        ): WorkManager = WorkManager.getInstance(context)
    }

}