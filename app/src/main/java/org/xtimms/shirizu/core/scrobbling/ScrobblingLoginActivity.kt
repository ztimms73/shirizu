package org.xtimms.shirizu.core.scrobbling

import android.net.Uri
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScrobblingLoginActivity : BaseOAuthLoginActivity() {

    override fun handleResult(data: Uri?) {
        when (data?.host) {
            "shikimori-auth" -> handleShikimori(data)
        }
    }

    private fun handleShikimori(data: Uri) {
        val code = data.getQueryParameter("code")
        if (code != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                shikimoriRepository.authorize(code)
                returnToSettings()
            }
        } else {
            shikimoriRepository.logout()
            returnToSettings()
        }
    }
}