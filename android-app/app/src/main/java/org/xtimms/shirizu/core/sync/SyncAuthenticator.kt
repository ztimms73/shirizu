package org.xtimms.shirizu.core.sync

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.network.CommonHeaders

class SyncAuthenticator(
    context: Context,
    private val account: Account,
    private val syncSettings: SyncSettings,
    private val authApi: SyncAuthApi,
) : Authenticator {

    private val accountManager = AccountManager.get(context)
    private val tokenType = context.getString(R.string.account_type_sync)

    override fun authenticate(route: Route?, response: Response): Request? {
        val newToken = tryRefreshToken() ?: return null
        accountManager.setAuthToken(account, tokenType, newToken)
        return response.request.newBuilder()
            .header(CommonHeaders.AUTHORIZATION, "Bearer $newToken")
            .build()
    }

    private fun tryRefreshToken() = runCatching {
        runBlocking {
            authApi.authenticate(
                syncSettings.host,
                account.name,
                accountManager.getPassword(account),
            )
        }
    }.getOrNull()
}