package org.xtimms.tokusho

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.StrictMode
import com.google.android.material.color.DynamicColors
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koitharu.kotatsu.parsers.MangaLoaderContext
import org.xtimms.tokusho.core.database.MangaDatabase
import org.xtimms.tokusho.core.updates.Updater
import org.xtimms.tokusho.crash.CrashActivity
import org.xtimms.tokusho.crash.GlobalExceptionHandler
import javax.inject.Inject
import javax.inject.Provider

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var database: Provider<MangaDatabase>

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        context = applicationContext
        packageInfo = packageManager.run {
            if (Build.VERSION.SDK_INT >= 33) getPackageInfo(
                packageName, PackageManager.PackageInfoFlags.of(0)
            ) else getPackageInfo(packageName, 0)
        }
        applicationScope = CoroutineScope(SupervisorJob())
        DynamicColors.applyToActivitiesIfAvailable(this)

        applicationScope.launch((Dispatchers.IO)) {
            try {
                Updater.deleteOutdatedApk()
            } catch (_: Throwable) {

            }
        }

        GlobalExceptionHandler.initialize(applicationContext, CrashActivity::class.java)
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

        lateinit var applicationScope: CoroutineScope
        lateinit var packageInfo: PackageInfo

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

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

}