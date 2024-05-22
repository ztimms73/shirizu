package org.xtimms.shirizu

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.StrictMode
import androidx.core.content.getSystemService
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import coil.ImageLoader
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
import org.xtimms.shirizu.core.database.ShirizuDatabase
import org.xtimms.shirizu.core.prefs.KotatsuAppSettings
import org.xtimms.shirizu.core.updates.Updater
import org.xtimms.shirizu.crash.CrashActivity
import org.xtimms.shirizu.crash.GlobalExceptionHandler
import org.xtimms.shirizu.utils.lang.processLifecycleScope
import org.xtimms.shirizu.work.WorkScheduleManager
import javax.inject.Inject
import javax.inject.Provider

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var database: Provider<ShirizuDatabase>

    @Inject
    lateinit var settings: KotatsuAppSettings

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var workScheduleManager: WorkScheduleManager

    @Inject
    lateinit var workManagerProvider: Provider<WorkManager>

    @Inject
    lateinit var imageLoader: ImageLoader

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        packageInfo = packageManager.run {
            if (Build.VERSION.SDK_INT >= 33) getPackageInfo(
                packageName, PackageManager.PackageInfoFlags.of(0)
            ) else getPackageInfo(packageName, 0)
        }
        DynamicColors.applyToActivitiesIfAvailable(this)
        connectivityManager = getSystemService()!!

        processLifecycleScope.launch(Dispatchers.IO) {
            try {
                Updater.deleteOutdatedApk(this@App)
            } catch (_: Throwable) {

            }
        }

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

        GlobalExceptionHandler.initialize(applicationContext, CrashActivity::class.java)
        workScheduleManager.init()
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
        lateinit var connectivityManager: ConnectivityManager

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