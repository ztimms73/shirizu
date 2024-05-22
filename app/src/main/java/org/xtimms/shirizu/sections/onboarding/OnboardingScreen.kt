package org.xtimms.shirizu.sections.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.xtimms.shirizu.core.onboarding.OnboardingScreen
import org.xtimms.shirizu.utils.lang.Screen

class OnboardingScreen : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val finishOnboarding: () -> Unit = {
            navigator.pop()
        }

        BackHandler(
            enabled = true,
            onBack = {
                // Prevent exiting if onboarding hasn't been completed
            },
        )

        OnboardingScreen(
            onComplete = finishOnboarding
        )
    }
}