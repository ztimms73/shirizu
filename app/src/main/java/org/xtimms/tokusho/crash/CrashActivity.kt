package org.xtimms.tokusho.crash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import org.xtimms.tokusho.LocalDarkTheme
import org.xtimms.tokusho.LocalDynamicColorSwitch
import org.xtimms.tokusho.MainActivity
import org.xtimms.tokusho.SettingsProvider
import org.xtimms.tokusho.ui.theme.TokushoTheme

@AndroidEntryPoint
class CrashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val exception = GlobalExceptionHandler.getThrowableFromIntent(intent)
        setContent {
            SettingsProvider {
                TokushoTheme(
                    darkTheme = LocalDarkTheme.current.isDarkTheme(),
                    isDynamicColorEnabled = LocalDynamicColorSwitch.current,
                    isHighContrastModeEnabled = LocalDarkTheme.current.isHighContrastModeEnabled,
                ) {
                    CrashScreen(
                        exception = exception,
                        onRestartClick = {
                            finishAffinity()
                            startActivity(Intent(this@CrashActivity, MainActivity::class.java))
                        },
                    )
                }
            }
        }
    }
}