package org.xtimms.shirizu.utils.system

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.xtimms.shirizu.utils.lang.processLifecycleScope

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.toast(@StringRes stringRes: Int) {
    toast(getString(stringRes))
}

fun Context.suspendToast(@StringRes stringRes: Int) {
    processLifecycleScope.launch(Dispatchers.Main) {
        toast(getString(stringRes))
    }
}
