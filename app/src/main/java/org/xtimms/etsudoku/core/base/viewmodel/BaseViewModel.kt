package org.xtimms.etsudoku.core.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.xtimms.etsudoku.core.base.event.UiEvent
import org.xtimms.etsudoku.core.base.state.UiState
import org.xtimms.etsudoku.utils.lang.EventFlow
import org.xtimms.etsudoku.utils.lang.MutableEventFlow
import org.xtimms.etsudoku.utils.lang.call
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException

abstract class BaseViewModel<S : UiState> : ViewModel(), UiEvent {

    @JvmField
    protected val loadingCounter = MutableStateFlow(0)

    @JvmField
    protected val errorEvent = MutableEventFlow<Throwable>()

    val onError: EventFlow<Throwable>
        get() = errorEvent

    protected abstract val mutableUiState: MutableStateFlow<S>
    val uiState: StateFlow<S> by lazy { mutableUiState.asStateFlow() }

    protected fun launchJob(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job = viewModelScope.launch(context + createErrorHandler(), start, block)

    @Suppress("UNCHECKED_CAST")
    fun setLoading(value: Boolean) {
        mutableUiState.update { it.setLoading(value) as S }
    }

    @Suppress("UNCHECKED_CAST")
    override fun showMessage(message: String?) {
        mutableUiState.update { it.setMessage(message ?: GENERIC_ERROR) as S }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onMessageDisplayed() {
        mutableUiState.update { it.setMessage(null) as S }
    }

    protected fun launchLoadingJob(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job = viewModelScope.launch(context + createErrorHandler(), start) {
        loadingCounter.increment()
        try {
            block()
        } finally {
            loadingCounter.decrement()
        }
    }

    protected fun MutableStateFlow<Int>.increment() = update { it + 1 }

    protected fun MutableStateFlow<Int>.decrement() = update { it - 1 }

    private fun createErrorHandler() = CoroutineExceptionHandler { _, throwable ->
        if (throwable !is CancellationException) {
            errorEvent.call(throwable)
        }
    }

    companion object {
        private const val GENERIC_ERROR = "Generic Error"
        const val FLOW_TIMEOUT = 5_000L
    }
}