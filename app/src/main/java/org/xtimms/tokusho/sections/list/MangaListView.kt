package org.xtimms.tokusho.sections.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.xtimms.tokusho.core.components.ScaffoldWithSmallTopAppBar

const val LIST_DESTINATION = "list"

@Composable
fun MangaListView(
    sourceName: String,
    navigateBack: () -> Unit,
    navigateToDetails: () -> Unit,
) {

    val scrollState = rememberScrollState()

    ScaffoldWithSmallTopAppBar(
        title = sourceName,
        navigateBack = navigateBack
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { navigateToDetails() }) {
                Text(text = "Click")
            }
        }
    }

}