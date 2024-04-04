package org.xtimms.etsudoku.core.sync

import android.accounts.AccountManager
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import org.xtimms.etsudoku.R
import org.xtimms.etsudoku.core.base.viewmodel.KotatsuBaseViewModel
import org.xtimms.etsudoku.utils.lang.MutableEventFlow
import org.xtimms.etsudoku.utils.lang.call
import javax.inject.Inject

@HiltViewModel
class SyncAuthViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val api: SyncAuthApi,
) : KotatsuBaseViewModel() {

    val onAccountAlreadyExists = MutableEventFlow<Unit>()
    val onTokenObtained = MutableEventFlow<SyncAuthResult>()
    val host = MutableStateFlow(context.getString(R.string.sync_host_default))

    init {
        launchJob(Dispatchers.Default) {
            val am = AccountManager.get(context)
            val accounts = am.getAccountsByType(context.getString(R.string.account_type_sync))
            if (accounts.isNotEmpty()) {
                onAccountAlreadyExists.call(Unit)
            }
        }
    }

    fun obtainToken(email: String, password: String) {
        val hostValue = host.value
        launchLoadingJob(Dispatchers.Default) {
            val token = api.authenticate(hostValue, email, password)
            val result = SyncAuthResult(host.value, email, password, token)
            onTokenObtained.call(result)
        }
    }
}