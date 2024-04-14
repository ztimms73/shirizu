package org.xtimms.shirizu.sections.settings.network

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.PhotoSizeSelectSmall
import androidx.compose.material.icons.outlined.SignalCellular4Bar
import androidx.compose.material.icons.outlined.SignalCellularConnectedNoInternet4Bar
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.VpnLock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.DialogSingleChoiceItem
import org.xtimms.shirizu.core.components.PreferenceItem
import org.xtimms.shirizu.core.components.PreferenceSubtitle
import org.xtimms.shirizu.core.components.PreferenceSwitch
import org.xtimms.shirizu.core.components.ScaffoldWithTopAppBar
import org.xtimms.shirizu.core.components.ShirizuDialog
import org.xtimms.shirizu.core.components.icons.ArrowDecisionOutline
import org.xtimms.shirizu.core.prefs.AppSettings
import org.xtimms.shirizu.core.prefs.AppSettings.getInt
import org.xtimms.shirizu.core.prefs.AppSettings.getString
import org.xtimms.shirizu.core.prefs.AppSettings.getValue
import org.xtimms.shirizu.core.prefs.AppSettings.updateInt
import org.xtimms.shirizu.core.prefs.AppSettings.updateString
import org.xtimms.shirizu.core.prefs.AppSettings.updateValue
import org.xtimms.shirizu.core.prefs.CELLULAR_DOWNLOAD
import org.xtimms.shirizu.core.prefs.DOH
import org.xtimms.shirizu.core.prefs.PROXY_ADDRESS
import org.xtimms.shirizu.core.prefs.PROXY_PASSWORD
import org.xtimms.shirizu.core.prefs.PROXY_PORT
import org.xtimms.shirizu.core.prefs.PROXY_TYPE
import org.xtimms.shirizu.core.prefs.PROXY_USER
import org.xtimms.shirizu.core.prefs.PreferenceStrings
import org.xtimms.shirizu.core.prefs.SSL_BYPASS
import org.xtimms.shirizu.core.prefs.WSRV
import org.xtimms.shirizu.utils.MaskVisualTransformation
import org.xtimms.shirizu.utils.NumberDefaults.INPUT_LENGTH
import org.xtimms.shirizu.utils.NumberDefaults.MASK
import org.xtimms.shirizu.utils.NumberDefaults.MAX_PORT
import org.xtimms.shirizu.utils.lang.intState
import java.net.Proxy

const val NETWORK_DESTINATION = "network"

@Composable
fun NetworkView(
    navigateBack: () -> Unit,
) {

    var showDOHDialog by remember { mutableStateOf(false) }
    var showProxyDialog by remember { mutableStateOf(false) }
    var showProxyAddressDialog by remember { mutableStateOf(false) }
    var showProxyPortDialog by remember { mutableStateOf(false) }
    var showProxyUsernameDialog by remember { mutableStateOf(false) }
    var showProxyPasswordDialog by remember { mutableStateOf(false) }

    var doh by DOH.intState
    var proxy by PROXY_TYPE.intState
    var address by remember(showProxyAddressDialog) { mutableStateOf(PROXY_ADDRESS.getString()) }
    var port by remember(showProxyPortDialog) { mutableIntStateOf(PROXY_PORT.getInt()) }
    var username by remember(showProxyUsernameDialog) { mutableStateOf(PROXY_USER.getString()) }
    var password by remember(showProxyPasswordDialog) { mutableStateOf(PROXY_PASSWORD.getString()) }

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
                PreferenceSubtitle(text = stringResource(id = R.string.general))
            }
            item {
                var isDownloadWithCellularEnabled by remember {
                    mutableStateOf(getValue(CELLULAR_DOWNLOAD))
                }
                PreferenceSwitch(
                    title = stringResource(R.string.download_with_cellular),
                    description = stringResource(R.string.download_with_cellular_desc),
                    icon = if (isDownloadWithCellularEnabled) Icons.Outlined.SignalCellular4Bar
                    else Icons.Outlined.SignalCellularConnectedNoInternet4Bar,
                    isChecked = isDownloadWithCellularEnabled,
                    onClick = {
                        isDownloadWithCellularEnabled = !isDownloadWithCellularEnabled
                        updateValue(
                            CELLULAR_DOWNLOAD,
                            isDownloadWithCellularEnabled
                        )
                    }
                )
            }
            item {
                PreferenceSubtitle(text = stringResource(id = R.string.advanced))
            }
            item {
                PreferenceItem(
                    title = stringResource(id = R.string.dns_over_https),
                    description = PreferenceStrings.getDOHDescRes(doh),
                    icon = Icons.Outlined.Dns
                ) { showDOHDialog = true }
            }
            item {
                PreferenceSwitch(
                    title = stringResource(id = R.string.images_optimization_proxy),
                    description = stringResource(id = R.string.images_optimization_proxy_desc),
                    icon = Icons.Outlined.PhotoSizeSelectSmall,
                    isChecked = isImageOptimizationEnabled,
                ) {
                    isImageOptimizationEnabled = !isImageOptimizationEnabled
                    updateValue(WSRV, isImageOptimizationEnabled)
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
                    updateValue(SSL_BYPASS, isSSLBypassEnabled)
                }
            }
            item {
                PreferenceSubtitle(text = stringResource(id = R.string.proxy))
            }
            item {
                PreferenceItem(
                    title = stringResource(id = R.string.proxy_type),
                    description = PreferenceStrings.getProxyDescRes(proxy),
                ) { showProxyDialog = true }
            }
            item {
                PreferenceItem(
                    enabled = proxy != Proxy.Type.DIRECT.ordinal,
                    title = stringResource(id = R.string.proxy_address),
                    description = address,
                ) { showProxyAddressDialog = true }
            }
            item {
                PreferenceItem(
                    enabled = proxy != Proxy.Type.DIRECT.ordinal,
                    title = stringResource(id = R.string.proxy_port),
                    description = port.toString()
                ) { showProxyPortDialog = true }
            }
            item {
                PreferenceSubtitle(text = stringResource(id = R.string.proxy_authorization))
            }
            item {
                PreferenceItem(
                    enabled = proxy != Proxy.Type.DIRECT.ordinal,
                    title = stringResource(id = R.string.proxy_username),
                    description = username,
                ) { showProxyUsernameDialog = true }
            }
            item {
                PreferenceItem(
                    enabled = proxy != Proxy.Type.DIRECT.ordinal,
                    title = stringResource(id = R.string.proxy_password),
                    description = String(CharArray(password.length) { '\u2022' }),
                ) { showProxyPasswordDialog = true }
            }
        }
    }
    if (showDOHDialog) {
        DOHSettingDialog(provider = doh,
            onDismissRequest = { showDOHDialog = false }) {
            doh = it
            DOH.updateInt(it)
        }
    }
    if (showProxyDialog) {
        ProxySettingDialog(type = proxy,
            onDismissRequest = { showProxyDialog = false }) {
            proxy = it
            PROXY_TYPE.updateInt(it)
        }
    }
    if (showProxyAddressDialog) {
        ProxyAddressSettingDialog(address = address,
            onDismissRequest = { showProxyAddressDialog = false }) {
            address = it
            PROXY_ADDRESS.updateString(it)
        }
    }
    if (showProxyPortDialog) {
        ProxyPortSettingDialog(port = port.toString(),
            onDismissRequest = { showProxyPortDialog = false }) {
            port = it
            PROXY_PORT.updateInt(it)
        }
    }
    if (showProxyUsernameDialog) {
        ProxyUsernameSettingDialog(username = username,
            onDismissRequest = { showProxyUsernameDialog = false }) {
            username = it
            PROXY_USER.updateString(it)
        }
    }
    if (showProxyPasswordDialog) {
        ProxyPasswordSettingDialog(password = password,
            onDismissRequest = { showProxyPasswordDialog = false }) {
            password = it
            PROXY_PASSWORD.updateString(it)
        }
    }
}

@Composable
fun DOHSettingDialog(
    provider: Int = 0,
    onDismissRequest: () -> Unit = {},
    onConfirm: (Int) -> Unit = {}
) {
    var dohProvider by remember { mutableIntStateOf(provider) }

    ShirizuDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.dismiss))
            }
        },
        icon = { Icon(Icons.Outlined.Dns, null) },
        title = {
            Text(stringResource(R.string.dns_over_https))
        }, confirmButton = {
            TextButton(onClick = {
                onConfirm(dohProvider)
                onDismissRequest()
            }) {
                Text(text = stringResource(R.string.confirm))
            }
        }, text = {
            Column {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .padding(horizontal = 24.dp),
                    text = stringResource(R.string.doh_desc),
                    style = MaterialTheme.typography.bodyLarge
                )
                LazyColumn {
                    for (i in 0..3) {
                        item {
                            DialogSingleChoiceItem(
                                text = PreferenceStrings.getDOHDescRes(i),
                                selected = dohProvider == i
                            ) {
                                dohProvider = i
                            }
                        }
                    }
                }
            }
        })
}

@Composable
fun ProxySettingDialog(
    type: Int = 0,
    onDismissRequest: () -> Unit = {},
    onConfirm: (Int) -> Unit = {}
) {
    var proxy by remember { mutableIntStateOf(type) }

    ShirizuDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.dismiss))
            }
        },
        icon = { Icon(Icons.Outlined.ArrowDecisionOutline, null) },
        title = {
            Text(stringResource(R.string.proxy_type))
        }, confirmButton = {
            TextButton(onClick = {
                onConfirm(proxy)
                onDismissRequest()
            }) {
                Text(text = stringResource(R.string.confirm))
            }
        }, text = {
            Column {
                LazyColumn {
                    for (i in 0..2) {
                        item {
                            DialogSingleChoiceItem(
                                text = PreferenceStrings.getProxyDescRes(i),
                                selected = proxy == i
                            ) {
                                proxy = i
                            }
                        }
                    }
                }
            }
        })
}

@Composable
fun ProxyAddressSettingDialog(
    address: String = "",
    onDismissRequest: () -> Unit = {},
    onConfirm: (String) -> Unit = {}
) {
    var proxyAddress by remember { mutableStateOf(address) }

    ShirizuDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.dismiss))
            }
        },
        title = {
            Text(stringResource(R.string.proxy_address))
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(proxyAddress)
                onDismissRequest()
            }) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        text = {
            OutlinedTextField(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                value = proxyAddress,
                onValueChange = { proxyAddress = it },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
        })
}

@Composable
fun ProxyPortSettingDialog(
    port: String = "",
    onDismissRequest: () -> Unit = {},
    onConfirm: (Int) -> Unit = {}
) {
    var proxyPort by remember { mutableStateOf(port) }

    ShirizuDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.dismiss))
            }
        },
        title = {
            Text(stringResource(R.string.proxy_port))
        },
        confirmButton = {
            TextButton(enabled = proxyPort.toInt() < MAX_PORT,
                onClick = {
                    onConfirm(proxyPort.toInt())
                    onDismissRequest()
                }) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        text = {
            OutlinedTextField(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                value = proxyPort,
                isError = proxyPort.toInt() > MAX_PORT,
                onValueChange = { it ->
                    if (it.length <= INPUT_LENGTH) {
                        proxyPort = it.filter { it.isDigit() }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Decimal
                ),
                visualTransformation = MaskVisualTransformation(MASK)
            )
        })
}

@Composable
fun ProxyUsernameSettingDialog(
    username: String = "",
    onDismissRequest: () -> Unit = {},
    onConfirm: (String) -> Unit = {}
) {
    var proxyUsername by remember { mutableStateOf(username) }

    ShirizuDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.dismiss))
            }
        },
        title = {
            Text(stringResource(R.string.proxy_username))
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(proxyUsername)
                onDismissRequest()
            }) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        text = {
            OutlinedTextField(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                value = proxyUsername,
                onValueChange = { proxyUsername = it },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
        })
}

@Composable
fun ProxyPasswordSettingDialog(
    password: String = "",
    onDismissRequest: () -> Unit = {},
    onConfirm: (String) -> Unit = {}
) {
    var proxyPassword by remember { mutableStateOf(password) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    ShirizuDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.dismiss))
            }
        },
        title = {
            Text(stringResource(R.string.proxy_password))
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(proxyPassword)
                onDismissRequest()
            }) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        text = {
            OutlinedTextField(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                value = proxyPassword,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                onValueChange = { proxyPassword = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
                trailingIcon = {
                    val image =
                        if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, null)
                    }
                }
            )
        })
}