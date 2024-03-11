package org.xtimms.tokusho.sections.history

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.collapsable
import org.xtimms.tokusho.core.screens.EmptyScreen
import org.xtimms.tokusho.ui.theme.TokushoTheme

const val HISTORY_DESTINATION = "history"

@Composable
fun HistoryView(
    topBarHeightPx: Float,
    padding: PaddingValues,
) {
    HistoryViewContent(
        topBarHeightPx = topBarHeightPx,
        padding = padding
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryViewContent(
    topBarHeightPx: Float,
    topBarOffsetY: Animatable<Float, AnimationVector1D> = Animatable(0f),
    padding: PaddingValues,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .collapsable(
                state = scrollState,
                topBarHeightPx = topBarHeightPx,
                topBarOffsetY = topBarOffsetY
            )
            .padding(padding)
    ) {
        EmptyScreen(
            icon = Icons.Outlined.History,
            title = R.string.empty_history_title,
            description = R.string.empty_history_description
        )
    }
}

@Preview
@Composable
fun HistoryPreview() {
    TokushoTheme {
        Surface {
            HistoryViewContent(
                padding = PaddingValues(),
                topBarHeightPx = 0f,
            )
        }
    }
}