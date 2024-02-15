package org.xtimms.tokusho

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.StrictMode
import com.google.android.material.color.DynamicColors
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.acra.ReportField
import org.acra.config.httpSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import org.acra.sender.HttpSender
import org.koitharu.kotatsu.parsers.MangaLoaderContext
import org.xtimms.tokusho.core.database.TokushoDatabase
import org.xtimms.tokusho.core.prefs.AppSettings
import org.xtimms.tokusho.core.updates.Updater
import org.xtimms.tokusho.utils.lang.processLifecycleScope
import javax.inject.Inject
import javax.inject.Provider

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var database: Provider<TokushoDatabase>

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        packageInfo = packageManager.run {
            if (Build.VERSION.SDK_INT >= 33) getPackageInfo(
                packageName, PackageManager.PackageInfoFlags.of(0)
            ) else getPackageInfo(packageName, 0)
        }
        DynamicColors.applyToActivitiesIfAvailable(this)

        processLifecycleScope.launch(Dispatchers.IO) {
            try {
                Updater.deleteOutdatedApk(this@App)
            } catch (_: Throwable) {

            }
        }

        // GlobalExceptionHandler.initialize(applicationContext, CrashActivity::class.java)
        if (AppSettings.isACRAEnabled()) {
            initAcra {
                buildConfigClass = BuildConfig::class.java
                reportFormat = StringFormat.JSON
                httpSender {
                    uri = BuildConfig.ACRA_URI
                    basicAuthLogin = BuildConfig.ACRA_AUTH_LOGIN
                    basicAuthPassword = BuildConfig.ACRA_AUTH_PASSWORD
                    httpMethod = HttpSender.Method.POST
                }
                reportContent = listOf(
                    ReportField.PACKAGE_NAME,
                    ReportField.INSTALLATION_ID,
                    ReportField.APP_VERSION_CODE,
                    ReportField.APP_VERSION_NAME,
                    ReportField.ANDROID_VERSION,
                    ReportField.PHONE_MODEL,
                    ReportField.STACK_TRACE,
                    ReportField.CRASH_CONFIGURATION,
                    ReportField.CUSTOM_DATA,
                )
            }
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        if (BuildConfig.DEBUG) {
            enableStrictMode()
        }
    }

    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build(),
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()
                .setClassInstanceLimit(MangaLoaderContext::class.java, 1)
                .penaltyLog()
                .build(),
        )
    }

    companion object {

        lateinit var packageInfo: PackageInfo

        @Suppress("DEPRECATION")
        fun getVersionReport(): String {
            val versionName = packageInfo.versionName
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                packageInfo.versionCode.toLong()
            }
            val release = if (Build.VERSION.SDK_INT >= 30) {
                Build.VERSION.RELEASE_OR_CODENAME
            } else {
                Build.VERSION.RELEASE
            }
            return StringBuilder().append("App version: $versionName ($versionCode)\n")
                .append("Device information: Android $release (API ${Build.VERSION.SDK_INT})\n")
                .append("Supported ABIs: ${Build.SUPPORTED_ABIS.contentToString()}\n").toString()
        }
    }

}