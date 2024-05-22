package org.xtimms.shirizu.core.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.ui.screens.InfoScreen
import org.xtimms.shirizu.utils.lang.materialSharedAxisX

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
) {

    var currentStep by rememberSaveable { mutableIntStateOf(0) }
    val steps = remember {
        listOf(
            SourcesStep(),
        )
    }
    val isLastStep = currentStep == steps.lastIndex

    BackHandler(enabled = currentStep != 0, onBack = { currentStep-- })

    InfoScreen(
        icon = Icons.Outlined.RocketLaunch,
        headingText1 = stringResource(R.string.onboarding_heading),
        subtitleText = stringResource(R.string.onboarding_description),
        acceptText = stringResource(
            if (isLastStep) {
                R.string.onboarding_action_finish
            } else {
                R.string.onboarding_action_next
            },
        ),
        canAccept = steps[currentStep].isComplete,
        onAcceptClick = {
            if (isLastStep) {
                onComplete()
            } else {
                currentStep++
            }
        },
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clip(MaterialTheme.shapes.small)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    materialSharedAxisX(
                        forward = targetState > initialState
                    )
                },
                label = "stepContent",
            ) {
                steps[it].Content()
            }
        }
    }
}