package org.xtimms.shirizu.sections.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.ShirizuAsyncImage
import org.xtimms.shirizu.core.components.Scaffold
import org.xtimms.shirizu.utils.composable.bodyWidth
import org.xtimms.shirizu.utils.lang.Tab

object ProfileTab : Tab {

    private val snackbarHostState = SnackbarHostState()

    override val options: TabOptions
        @Composable
        get() {
            val image = Icons.Outlined.AccountCircle
            return TabOptions(
                index = 3u,
                title = stringResource(R.string.profile),
                icon = rememberVectorPainter(image),
            )
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        Scaffold(
            snackbarHost = { snackbarHostState }
        ) {
            ProfileScreen()
        }
    }
}