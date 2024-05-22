package org.xtimms.shirizu.sections.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.StateFlow
import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.ui.screens.EmptyScreen
import org.xtimms.shirizu.core.ui.screens.EmptyScreenAction
import org.xtimms.shirizu.core.ui.screens.LoadingScreen
import org.xtimms.shirizu.utils.system.getDisplayMessage

@Composable
fun MangaListContent(
    mangaList: List<Manga>,
    columns: GridCells,
    snackbarHostState: SnackbarHostState,
    contentPadding: PaddingValues,
    onMangaClick: (Manga) -> Unit,
    onMangaLongClick: (Manga) -> Unit,
) {
    val context = LocalContext.current

    val getErrorMessage: (LoadState.Error) -> String = { state ->
        state.error.getDisplayMessage(context.resources)
    }

    if (mangaList.isEmpty()) {
        EmptyScreen(
            icon = Icons.Outlined.ErrorOutline,
            modifier = Modifier.padding(contentPadding),
            message = "",
            summary = "",
            actions = persistentListOf(
                EmptyScreenAction(
                    stringRes = R.string.action_retry,
                    icon = Icons.Outlined.Refresh,
                    onClick = {  },
                ),
            )
        )

        return
    }

    MangaList(
        mangaList = mangaList,
        contentPadding = contentPadding,
        onMangaClick = onMangaClick,
        onMangaLongClick = onMangaLongClick,
        columns = columns
    )
}