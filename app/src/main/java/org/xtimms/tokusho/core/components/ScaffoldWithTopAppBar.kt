package org.xtimms.tokusho.core.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithTopAppBar(
    title: String,
    navigateBack: () -> Unit,
    floatingActionButton: @Composable (() -> Unit) = {},
    contentWindowInsets: WindowInsets = WindowInsets.systemBars,
    content: @Composable (PaddingValues) -> Unit
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            DefaultTopAppBar(
                title = title,
                scrollBehavior = topAppBarScrollBehavior,
                navigateBack = navigateBack
            )
        },
        floatingActionButton = floatingActionButton,
        contentWindowInsets = contentWindowInsets,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithSmallTopAppBar(
    title: String,
    navigateBack: () -> Unit,
    floatingActionButton: @Composable (() -> Unit) = {},
    contentWindowInsets: WindowInsets = WindowInsets.systemBars,
    content: @Composable (PaddingValues) -> Unit
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            SmallTopAppBar(
                title = title,
                scrollBehavior = topAppBarScrollBehavior,
                navigateBack = navigateBack
            )
        },
        floatingActionButton = floatingActionButton,
        contentWindowInsets = contentWindowInsets,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithClassicTopAppBar(
    title: String,
    navigateBack: () -> Unit,
    floatingActionButton: @Composable (() -> Unit) = {},
    contentWindowInsets: WindowInsets = WindowInsets.systemBars,
    content: @Composable (PaddingValues) -> Unit
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            ClassicTopAppBar(
                title = title,
                scrollBehavior = topAppBarScrollBehavior,
                navigateBack = navigateBack
            )
        },
        floatingActionButton = floatingActionButton,
        contentWindowInsets = contentWindowInsets,
        content = content
    )
}