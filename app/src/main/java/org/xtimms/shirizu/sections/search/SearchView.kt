package org.xtimms.shirizu.sections.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.BackIconButton
import org.xtimms.shirizu.core.ui.screens.EmptyScreen
import org.xtimms.shirizu.ui.theme.ShirizuTheme

const val SEARCH_DESTINATION = "search"

@Composable
fun SearchHostView(
    padding: PaddingValues,
    isCompactScreen: Boolean,
    navigateBack: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val performSearch = remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(top = padding.calculateTopPadding())
            .fillMaxWidth()
    ) {
        TextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .height(64.dp),
            placeholder = { Text(text = stringResource(R.string.search)) },
            leadingIcon = {
                if (isCompactScreen) BackIconButton(onClick = navigateBack)
            },
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                }
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
        SearchView(
            query = query,
            performSearch = performSearch,
            showAsGrid = !isCompactScreen,
            contentPadding = PaddingValues(bottom = padding.calculateBottomPadding()),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView(
    query: String,
    performSearch: MutableState<Boolean>,
    showAsGrid: Boolean,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val context = LocalContext.current

    EmptyScreen(
        icon = Icons.Outlined.SearchOff,
        title = R.string.nothing_found,
        description = R.string.nothing_found_summary
    )
}

@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    ShirizuTheme {
        SearchHostView(
            isCompactScreen = true,
            padding = PaddingValues(),
            navigateBack = {},
        )
    }
}