package org.xtimms.shirizu.core.base.event

interface UiEvent {
    fun showMessage(message: String?)
    fun onMessageDisplayed()
}