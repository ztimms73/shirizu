package org.xtimms.etsudoku.sections.settings.network

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.PhotoSizeSelectSmall
import androidx.compose.material.icons.outlined.VpnLock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.xtimms.etsudoku.R
import org.xtimms.etsudoku.core.components.PreferenceItem
import org.xtimms.etsudoku.core.components.PreferenceSwitch
import org.xtimms.etsudoku.core.components.ScaffoldWithTopAppBar
import org.xtimms.etsudoku.core.components.icons.ArrowDecisionOutline
import org.xtimms.etsudoku.core.prefs.AppSettings
import org.xtimms.etsudoku.core.prefs.SSL_BYPASS
import org.xtimms.etsudoku.core.prefs.WSRV

const val NETWORK_DESTINATION = "network"

@Composable
fun NetworkView(
    navigateBack: () -> Unit,
) {

    var isSSLBypassEnabled by remember {
        mutableStateOf(AppSettings.isSSLBypassEnabled())
    }

    var isImageOptimizationEnabled by remember {
        mutableStateOf(AppSettings.isImagesProxyEnabled())
    }

    ScaffoldWithTopAppBar(
        title = stringResource(R.string.network),
        navigateBack = navigateBack
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
        ) {
            item {
                PreferenceItem(
                    title = stringResource(id = R.string.proxy),
                    description = "",
                    icon = Icons.Outlined.ArrowDecisionOutline
                )
            }
            item {
                PreferenceItem(
                    title = stringResource(id = R.string.dns_over_https),
                    description = "",
                    icon = Icons.Outlined.Dns
                )
            }
            item {
                PreferenceSwitch(
                    title = stringResource(id = R.string.images_optimization_proxy),
                    description = stringResource(id = R.string.images_optimization_proxy_desc),
                    icon = Icons.Outlined.PhotoSizeSelectSmall,
                    isChecked = isImageOptimizationEnabled,
                ) {
                    isImageOptimizationEnabled = !isImageOptimizationEnabled
                    AppSettings.updateValue(WSRV, isImageOptimizationEnabled)
                }
            }
            item {
                PreferenceSwitch(
                    title = stringResource(id = R.string.ignore_ssl_errors),
                    description = stringResource(id = R.string.ignore_ssl_errors_desc),
                    icon = Icons.Outlined.VpnLock,
                    isChecked = isSSLBypassEnabled,
                ) {
                    isSSLBypassEnabled = !isSSLBypassEnabled
                    AppSettings.updateValue(SSL_BYPASS, isSSLBypassEnabled)
                }
            }
        }
    }

}