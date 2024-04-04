package org.xtimms.etsudoku.crash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import org.xtimms.etsudoku.LocalDarkTheme
import org.xtimms.etsudoku.LocalDynamicColorSwitch
import org.xtimms.etsudoku.MainActivity
import org.xtimms.etsudoku.SettingsProvider
import org.xtimms.etsudoku.ui.theme.EtsudokuTheme

@AndroidEntryPoint
class CrashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val exception = GlobalExceptionHandler.getThrowableFromIntent(intent)
        setContent {
            SettingsProvider {
                EtsudokuTheme(
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