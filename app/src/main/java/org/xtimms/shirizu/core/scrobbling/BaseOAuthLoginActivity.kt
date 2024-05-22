package org.xtimms.shirizu.core.scrobbling

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.xtimms.shirizu.MainActivity
import org.xtimms.shirizu.core.scrobbling.services.shikimori.data.ShikimoriRepository
import org.xtimms.shirizu.core.ui.screens.LoadingScreen
import javax.inject.Inject

abstract class BaseOAuthLoginActivity : ComponentActivity() {

    @Inject
    internal lateinit var shikimoriRepository: ShikimoriRepository

    abstract fun handleResult(data: Uri?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LoadingScreen()
        }

        handleResult(intent.data)
    }

    internal fun returnToSettings() {
        finish()

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(intent)
    }
}