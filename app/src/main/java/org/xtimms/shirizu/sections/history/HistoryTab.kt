package org.xtimms.shirizu.sections.history

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabOptions
import kotlinx.coroutines.channels.Channel
import org.xtimms.shirizu.R
import org.xtimms.shirizu.sections.details.DetailsScreen
import org.xtimms.shirizu.sections.library.history.HistoryScreen
import org.xtimms.shirizu.sections.library.history.HistoryScreenModel
import org.xtimms.shirizu.utils.lang.Tab

object HistoryTab : Tab {

    private val snackbarHostState = SnackbarHostState()

    private val resumeLastChapterReadEvent = Channel<Unit>()

    override val options: TabOptions
        @Composable
        get() {
            val image = Icons.Outlined.History
            return TabOptions(
                index = 1u,
                title = stringResource(R.string.history),
                icon = rememberVectorPainter(image),
            )
        }

    override suspend fun onReselect(navigator: Navigator) {
        resumeLastChapterReadEvent.send(Unit)
    }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val screenModel = getScreenModel<HistoryScreenModel>()
        val state by screenModel.state.collectAsState()

        /*HistoryScreen(
            state = state,
            snackbarHostState = snackbarHostState,
            onSearchQueryChange = {  },
            onClick = { navigator.push(DetailsScreen(it)) },
            onDialogChange = {  },
        )*/
    }
}