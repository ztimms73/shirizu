package org.xtimms.shirizu.sections.settings.about

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.util.htmlReadyLicenseContent
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.AppBar
import org.xtimms.shirizu.core.components.Scaffold
import org.xtimms.shirizu.utils.lang.Screen

@OptIn(ExperimentalMaterial3Api::class)
class OpenSourceLicensesScreen : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        Scaffold(
            topBar = { scrollBehavior ->
                AppBar(
                    title = stringResource(R.string.open_source_licenses),
                    navigateUp = navigator::pop,
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { contentPadding ->
            LibrariesContainer(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = contentPadding,
                onLibraryClick = {
                    navigator.push(
                        LicenseScreen(
                            name = it.library.name,
                            website = it.library.website,
                            license = it.library.licenses.firstOrNull()?.htmlReadyLicenseContent.orEmpty(),
                        )
                    )
                },
            )
        }
    }
}