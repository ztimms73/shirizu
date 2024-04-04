package org.xtimms.etsudoku.core.base.event

interface UiEvent {
    fun showMessage(message: String?)
    fun onMessageDisplayed()
}