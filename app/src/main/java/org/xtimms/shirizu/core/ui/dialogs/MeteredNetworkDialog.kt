package org.xtimms.shirizu.core.ui.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SignalCellularConnectedNoInternet4Bar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.BottomButtonShape
import org.xtimms.shirizu.core.components.MiddleButtonShape
import org.xtimms.shirizu.core.components.ShirizuDialogButtonVariant
import org.xtimms.shirizu.core.components.ShirizuDialogVariant
import org.xtimms.shirizu.core.components.TopButtonShape

@Composable
@Preview
fun MeteredNetworkDialog(
    onDismissRequest: () -> Unit = {},
    onAllowOnceConfirm: () -> Unit = {},
    onAllowAlwaysConfirm: () -> Unit = {},
) {
    ShirizuDialogVariant(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(
                imageVector = Icons.Outlined.SignalCellularConnectedNoInternet4Bar,
                contentDescription = null
            )
        },
        title = { Text(text = stringResource(id = R.string.download_with_cellular_request)) },
        buttons = {
            ShirizuDialogButtonVariant(
                text = stringResource(id = R.string.allow_always),
                shape = TopButtonShape
            ) {
                onAllowAlwaysConfirm()
            }
            ShirizuDialogButtonVariant(
                text = stringResource(id = R.string.allow_once),
                shape = MiddleButtonShape
            ) {
                onAllowOnceConfirm()
            }
            ShirizuDialogButtonVariant(
                text = stringResource(id = R.string.dont_allow),
                shape = BottomButtonShape
            ) {
                onDismissRequest()
            }
        },
    )
}