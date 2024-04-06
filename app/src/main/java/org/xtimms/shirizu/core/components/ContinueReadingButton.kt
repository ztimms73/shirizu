package org.xtimms.shirizu.core.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.xtimms.shirizu.R
import org.xtimms.shirizu.sections.history.HISTORY_DESTINATION
import org.xtimms.shirizu.sections.reader.READER_DESTINATION

@Composable
fun ContinueReadingButton(
    navController: NavController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val isVisible by remember {
        derivedStateOf {
            when (navBackStackEntry?.destination?.route) {
                HISTORY_DESTINATION, null -> true
                else -> false
            }
        }
    }

    val fabScale by animateFloatAsState(
        targetValue = when (navBackStackEntry?.destination?.route) {
            HISTORY_DESTINATION, null -> 1f
            else -> 0f
        },
        animationSpec = tween(150), label = "elevation"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300, delayMillis = 150)) +
                    scaleIn(
                        initialScale = 0.92f,
                        animationSpec = tween(300, delayMillis = 150)
                    ),
        exit = fadeOut(animationSpec = tween(0))
    ) {
        androidx.compose.material3.ExtendedFloatingActionButton(
            onClick = {
                navController.navigate(
                    READER_DESTINATION
                )
            },
            modifier = Modifier.padding(8.dp),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 4.dp
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.LocalLibrary,
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.continue_reading),
                modifier = Modifier.padding(start = 16.dp, end = 8.dp)
            )
        }
    }
}