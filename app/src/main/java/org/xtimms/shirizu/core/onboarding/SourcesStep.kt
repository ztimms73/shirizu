package org.xtimms.shirizu.core.onboarding

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

internal class SourcesStep : OnboardingStep {

    override val isComplete: Boolean = true

    @Composable
    override fun Content() {
        Text(text = "Hello")
    }
}