package org.xtimms.shirizu.sections.settings.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.google.android.material.textview.MaterialTextView
import kotlinx.collections.immutable.persistentListOf
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.AppBar
import org.xtimms.shirizu.core.components.AppBarActions
import org.xtimms.shirizu.core.components.Scaffold
import org.xtimms.shirizu.core.components.ScaffoldWithClassicTopAppBar
import org.xtimms.shirizu.utils.lang.Screen

@OptIn(ExperimentalMaterial3Api::class)
class LicenseScreen(
    private val name: String,
    private val website: String?,
    private val license: String,
) : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val uriHandler = LocalUriHandler.current

        Scaffold(
            topBar = {
                AppBar(
                    title = name,
                    navigateUp = navigator::pop,
                    actions = {
                        if (!website.isNullOrEmpty()) {
                            AppBarActions(
                                persistentListOf(
                                    AppBar.Action(
                                        title = stringResource(R.string.website),
                                        icon = Icons.Default.Public,
                                        onClick = { uriHandler.openUri(website) },
                                    ),
                                ),
                            )
                        }
                    },
                    scrollBehavior = it,
                )
            },
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(contentPadding)
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
}