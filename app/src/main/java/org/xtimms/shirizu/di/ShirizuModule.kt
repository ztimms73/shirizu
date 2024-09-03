package org.xtimms.shirizu.di

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
import org.xtimms.shirizu.BuildConfig
import org.xtimms.shirizu.core.cache.CacheDir
import org.xtimms.shirizu.core.database.ShirizuDatabase
import org.xtimms.shirizu.core.model.LocalManga
import org.xtimms.shirizu.core.network.MangaHttpClient
import org.xtimms.shirizu.core.network.interceptors.ImageProxyInterceptor
import org.xtimms.shirizu.core.os.NetworkState
import org.xtimms.shirizu.core.parser.MangaLoaderContextImpl
import org.xtimms.shirizu.core.parser.MangaRepository
import org.xtimms.shirizu.core.parser.favicon.FaviconFetcher
import org.xtimms.shirizu.core.parser.local.LocalStorageChanges
import org.xtimms.shirizu.sections.reader.thumbnails.MangaPageFetcher
import org.xtimms.shirizu.utils.CoilImageGetter
import org.xtimms.shirizu.utils.system.connectivityManager
import org.xtimms.shirizu.utils.system.isLowRamDevice
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ShirizuModule {

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
        ): ShirizuDatabase {
            return ShirizuDatabase(context)
        }

        @Provides
        @Singleton
        fun provideCoil(
            @ApplicationContext context: Context,
            @MangaHttpClient okHttpClientProvider: Provider<OkHttpClient>,
            mangaRepositoryFactory: MangaRepository.Factory,
            imageProxyInterceptor: ImageProxyInterceptor,
            pageFetcherFactory: MangaPageFetcher.Factory,
        ): ImageLoader {
            val diskCacheFactory = {
                val rootDir = context.externalCacheDir ?: context.cacheDir
                DiskCache.Builder()
                    .directory(rootDir.resolve(CacheDir.THUMBS.dir))
                    .build()
            }
            val okHttpClientLazy = lazy {
                okHttpClientProvider.get().newBuilder().cache(null).build()
            }
            return ImageLoader.Builder(context)
                .crossfade(500)
                .okHttpClient { okHttpClientLazy.value }
                .interceptorDispatcher(Dispatchers.Default)
                .fetcherDispatcher(Dispatchers.IO)
                .decoderDispatcher(Dispatchers.Default)
                .transformationDispatcher(Dispatchers.Default)
                .diskCache(diskCacheFactory)
                .respectCacheHeaders(false)
                .networkObserverEnabled(false)
                .logger(if (BuildConfig.DEBUG) DebugLogger() else null)
                .allowRgb565(context.isLowRamDevice())
                .components(
                    ComponentRegistry.Builder()
                        .add(FaviconFetcher.Factory(context, okHttpClientLazy, mangaRepositoryFactory))
                        .add(pageFetcherFactory)
                        .add(imageProxyInterceptor)
                        .build(),
                ).build()
        }

        @Provides
        @Singleton
        @LocalStorageChanges
        fun provideMutableLocalStorageChangesFlow(): MutableSharedFlow<LocalManga?> =
            MutableSharedFlow()

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