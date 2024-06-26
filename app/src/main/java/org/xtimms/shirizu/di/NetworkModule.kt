package org.xtimms.shirizu.di

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
import org.xtimms.shirizu.core.network.BaseHttpClient
import org.xtimms.shirizu.core.network.MangaHttpClient
import org.xtimms.shirizu.core.network.bypassSSLErrors
import org.xtimms.shirizu.core.network.cookies.AndroidCookieJar
import org.xtimms.shirizu.core.network.cookies.MutableCookieJar
import org.xtimms.shirizu.core.network.cookies.PreferencesCookieJar
import org.xtimms.shirizu.core.network.doh.DoHManager
import org.xtimms.shirizu.core.network.interceptors.CacheLimitInterceptor
import org.xtimms.shirizu.core.network.interceptors.CloudflareInterceptor
import org.xtimms.shirizu.core.network.interceptors.CommonHeadersInterceptor
import org.xtimms.shirizu.core.network.interceptors.GZipInterceptor
import org.xtimms.shirizu.core.network.interceptors.RateLimitInterceptor
import org.xtimms.shirizu.core.network.proxy.AppProxySelector
import org.xtimms.shirizu.core.network.proxy.ProxyAuthenticator
import org.xtimms.shirizu.core.prefs.AppSettings
import org.xtimms.shirizu.data.LocalStorageManager
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
            proxySelector(AppProxySelector())
            proxyAuthenticator(ProxyAuthenticator())
            dns(DoHManager(cache))
            /*if (AppSettings.isSSLBypassEnabled()) {
                bypassSSLErrors()
            }*/
            cache(cache)
            addInterceptor(GZipInterceptor())
            addInterceptor(CloudflareInterceptor())
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