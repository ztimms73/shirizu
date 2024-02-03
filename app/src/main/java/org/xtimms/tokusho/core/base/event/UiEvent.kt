package org.xtimms.tokusho.core.base.event

interface UiEvent {
    fun showMessage(message: String?)
    fun onMessageDisplayed()
}