package org.xtimms.shirizu.core.base

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.viewbinding.ViewBinding

@Suppress("LeakingThis")
abstract class BaseActivity<B : ViewBinding> :
    AppCompatActivity() {

    lateinit var viewBinding: B
        private set

    private var defaultStatusBarColor = Color.TRANSPARENT

    override fun onCreate(savedInstanceState: Bundle?) {
        putDataToExtras(intent)
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    override fun onNewIntent(intent: Intent?) {
        putDataToExtras(intent)
        super.onNewIntent(intent)
    }

    @Deprecated("Use ViewBinding", level = DeprecationLevel.ERROR)
    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
    }

    @Deprecated("Use ViewBinding", level = DeprecationLevel.ERROR)
    override fun setContentView(view: View?) {
        super.setContentView(view)
    }

    protected fun setContentView(binding: B) {
        this.viewBinding = binding
        super.setContentView(binding.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        dispatchNavigateUp()
        return true
    }

    protected fun isDarkAmoledTheme(): Boolean {
        val uiMode = resources.configuration.uiMode
        return uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    protected open fun dispatchNavigateUp() {
        val upIntent = parentActivityIntent
        if (upIntent != null) {
            if (!navigateUpTo(upIntent)) {
                startActivity(upIntent)
            }
        } else {
            finishAfterTransition()
        }
    }

    private fun putDataToExtras(intent: Intent?) {
        intent?.putExtra(EXTRA_DATA, intent.data)
    }

    companion object {

        const val EXTRA_DATA = "data"
    }
}