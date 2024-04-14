package org.xtimms.shirizu.crash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import org.xtimms.shirizu.LocalDarkTheme
import org.xtimms.shirizu.LocalDynamicColorSwitch
import org.xtimms.shirizu.LocalWindowWidthState
import org.xtimms.shirizu.MainActivity
import org.xtimms.shirizu.SettingsProvider
import org.xtimms.shirizu.ui.theme.ShirizuTheme

@AndroidEntryPoint
class CrashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val exception = GlobalExceptionHandler.getThrowableFromIntent(intent)
        setContent {
            SettingsProvider(LocalWindowWidthState.current) {
                ShirizuTheme(
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