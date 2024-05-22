package org.xtimms.shirizu.sections.library.updates

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.ui.screens.TabContent

@Composable
fun Screen.updatesTab(): TabContent {

    return TabContent(
        titleRes = R.string.updates,
        content = { contentPadding, snackbarHostState ->

        }
    )

}