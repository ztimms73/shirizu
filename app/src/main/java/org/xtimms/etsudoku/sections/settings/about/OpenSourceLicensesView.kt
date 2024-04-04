package org.xtimms.etsudoku.sections.settings.about

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.util.htmlReadyLicenseContent
import org.xtimms.etsudoku.R
import org.xtimms.etsudoku.core.components.ScaffoldWithClassicTopAppBar

const val LICENSES_DESTINATION = "licenses"

@Composable
fun OpenSourceLicensesView(
    navigateBack: () -> Unit,
    navigateToLicensePage: (String, String?, String?) -> Unit
) {

    ScaffoldWithClassicTopAppBar(
        title = stringResource(R.string.about),
        navigateBack = navigateBack
    ) { padding ->
        LibrariesContainer(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = padding,
            onLibraryClick = {
                navigateToLicensePage(
                    it.library.name,
                    it.library.website,
                    it.library.licenses.firstOrNull()?.htmlReadyLicenseContent.orEmpty()
                )
            },
        )
    }
}