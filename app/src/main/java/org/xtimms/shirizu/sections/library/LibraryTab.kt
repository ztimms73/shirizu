package org.xtimms.shirizu.sections.library

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.navigator.tab.TabOptions
import kotlinx.collections.immutable.persistentListOf
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.ui.screens.TabbedScreen
import org.xtimms.shirizu.sections.library.history.historyTab
import org.xtimms.shirizu.sections.library.shelves.shelvesTab
import org.xtimms.shirizu.sections.library.updates.updatesTab
import org.xtimms.shirizu.utils.lang.NoLiftingAppBarScreen
import org.xtimms.shirizu.utils.lang.Tab

class LibraryTab : Tab, NoLiftingAppBarScreen {

    override val options: TabOptions
        @Composable
        get() {
            val image = Icons.AutoMirrored.Outlined.LibraryBooks
            return TabOptions(
                index = 0u,
                title = stringResource(R.string.nav_library),
                icon = rememberVectorPainter(image),
            )
        }

    @Composable
    override fun Content() {
        val context = LocalContext.current

        TabbedScreen(
            titleRes = R.string.nav_library,
            tabs = persistentListOf(
                historyTab(),
                shelvesTab(),
                updatesTab()
            ),
            startIndex = 0, // TODO maybe customizable
            searchQuery = "",
            onChangeSearchQuery = {  },
        )
    }
}