package org.xtimms.shirizu.core.onboarding

import androidx.compose.runtime.Composable

internal interface OnboardingStep {

    val isComplete: Boolean

    @Composable
    fun Content()
}