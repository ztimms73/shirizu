package org.xtimms.tokusho.sections.settings.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.google.android.material.textview.MaterialTextView
import org.xtimms.tokusho.core.components.ScaffoldWithClassicTopAppBar

const val LICENSE_NAME_ARGUMENT = "{name}"
const val LICENSE_WEBSITE_ARGUMENT = "{website}"
const val LICENSE_CONTENT_ARGUMENT = "{content}"
const val LICENSE_DESTINATION =
    "license/${LICENSE_NAME_ARGUMENT}?${LICENSE_WEBSITE_ARGUMENT}?${LICENSE_CONTENT_ARGUMENT}"

@Composable
fun LicenseView(
    name: String,
    website: String,
    license: String,
    navigateBack: () -> Unit
) {

    val uriHandler = LocalUriHandler.current

    ScaffoldWithClassicTopAppBar(
        title = name,
        navigateBack = navigateBack,
        actions = {
            IconButton(onClick = { uriHandler.openUri(website) }) {
                Icon(imageVector = Icons.Outlined.Public, contentDescription = null)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
        ) {
            HtmlLicenseText(html = license)
        }
    }
}

@Composable
private fun HtmlLicenseText(html: String) {
    AndroidView(
        factory = {
            MaterialTextView(it)
        },
        update = {
            it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
        },
    )
}