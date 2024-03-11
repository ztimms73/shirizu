package org.xtimms.tokusho.core.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import kotlinx.collections.immutable.ImmutableList
import org.xtimms.tokusho.core.components.ActionButton
import org.xtimms.tokusho.utils.composable.secondaryItemAlpha
import kotlin.random.Random

data class EmptyScreenAction(
    val stringRes: Int,
    val icon: ImageVector,
    val onClick: () -> Unit,
)

@Composable
fun EmptyScreen(
    icon: ImageVector,
    @StringRes title: Int,
    @StringRes description: Int,
    modifier: Modifier = Modifier,
    actions: ImmutableList<EmptyScreenAction>? = null,
) {
    EmptyScreen(
        icon = icon,
        message = stringResource(title),
        summary = stringResource(description),
        modifier = modifier,
        actions = actions,
    )
}

@Composable
fun EmptyScreen(
    icon: ImageVector,
    message: String,
    summary: String,
    modifier: Modifier = Modifier,
    actions: ImmutableList<EmptyScreenAction>? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(96.dp).secondaryItemAlpha()
        )

        Text(
            text = message,
            modifier = Modifier
                .paddingFromBaseline(top = 24.dp)
                .secondaryItemAlpha(),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )

        Text(
            text = summary,
            modifier = Modifier
                .paddingFromBaseline(top = 24.dp)
                .secondaryItemAlpha(),
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
        )

        if (!actions.isNullOrEmpty()) {
            Row(
                modifier = Modifier
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                actions.fastForEach {
                    ActionButton(
                        modifier = Modifier.weight(1f),
                        title = stringResource(it.stringRes),
                        icon = it.icon,
                        onClick = it.onClick,
                    )
                }
            }
        }
    }
}
