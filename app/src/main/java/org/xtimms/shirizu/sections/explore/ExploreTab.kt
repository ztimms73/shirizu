package org.xtimms.shirizu.sections.explore

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.navigator.tab.TabOptions
import kotlinx.collections.immutable.persistentListOf
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.ui.screens.TabbedScreen
import org.xtimms.shirizu.sections.explore.catalog.catalogTab
import org.xtimms.shirizu.sections.explore.sources.sourcesTab
import org.xtimms.shirizu.utils.lang.NoLiftingAppBarScreen
import org.xtimms.shirizu.utils.lang.Tab

data class ExploreTab(
    private val toCatalog: Boolean = false,
) : Tab, NoLiftingAppBarScreen {

    override val options: TabOptions
        @Composable
        get() {
            val image = Icons.Outlined.Explore
            return TabOptions(
                index = 3u,
                title = stringResource(R.string.nav_explore),
                icon = rememberVectorPainter(image),
            )
        }

    @Composable
    override fun Content() {
        val context = LocalContext.current

        TabbedScreen(
            titleRes = R.string.nav_explore,
            tabs = persistentListOf(
                sourcesTab(),
                catalogTab()
            ),
            startIndex = 1.takeIf { toCatalog },
            searchQuery = "",
            onChangeSearchQuery = {  },
        )
    }
}