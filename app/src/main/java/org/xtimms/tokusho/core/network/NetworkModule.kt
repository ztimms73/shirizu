package org.xtimms.tokusho.core.network

import android.content.Context
import android.util.AndroidRuntimeException
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import org.xtimms.tokusho.core.network.cookies.AndroidCookieJar
import org.xtimms.tokusho.core.network.cookies.MutableCookieJar
import org.xtimms.tokusho.core.network.cookies.PreferencesCookieJar
import org.xtimms.tokusho.core.network.interceptors.CacheLimitInterceptor
import org.xtimms.tokusho.core.network.interceptors.CommonHeadersInterceptor
import org.xtimms.tokusho.core.network.interceptors.GZipInterceptor
import org.xtimms.tokusho.core.network.interceptors.RateLimitInterceptor
import org.xtimms.tokusho.data.LocalStorageManager
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NetworkModule {

    @Binds
    fun bindCookieJar(androidCookieJar: MutableCookieJar): CookieJar

    companion object {

        @Provides
        @Singleton
        fun provideCookieJar(
            @ApplicationContext context: Context
        ): MutableCookieJar = try {
            AndroidCookieJar()
        } catch (e: AndroidRuntimeException) {
            PreferencesCookieJar(context)
        }

        @Provides
        @Singleton
        fun provideHttpCache(
            localStorageManager: LocalStorageManager,
        ): Cache = localStorageManager.createHttpCache()

        @Provides
        @Singleton
        @BaseHttpClient
        fun provideBaseHttpClient(
            cache: Cache,
            cookieJar: CookieJar,
        ): OkHttpClient = OkHttpClient.Builder().apply {
            connectTimeout(20, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
            writeTimeout(20, TimeUnit.SECONDS)
            cookieJar(cookieJar)
            cache(cache)
            addInterceptor(GZipInterceptor())
            addInterceptor(RateLimitInterceptor())
        }.build()

        @Provides
        @Singleton
        @MangaHttpClient
        fun provideMangaHttpClient(
            @BaseHttpClient baseClient: OkHttpClient,
            commonHeadersInterceptor: CommonHeadersInterceptor,
        ): OkHttpClient = baseClient.newBuilder().apply {
            addNetworkInterceptor(CacheLimitInterceptor())
            addInterceptor(commonHeadersInterceptor)
        }.build()

    }

}