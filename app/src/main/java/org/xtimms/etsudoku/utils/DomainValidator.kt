package org.xtimms.etsudoku.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.annotation.CallSuper
import okhttp3.HttpUrl
import org.xtimms.etsudoku.R
import org.xtimms.etsudoku.utils.system.getDisplayMessage
import java.lang.ref.WeakReference

class DomainValidator : EditTextValidator() {

    override fun validate(text: String): ValidationResult {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return ValidationResult.Success
        }
        return if (!checkCharacters(trimmed)) {
            ValidationResult.Failed(context.getString(R.string.invalid_domain_message))
        } else {
            ValidationResult.Success
        }
    }

    private fun checkCharacters(value: String): Boolean = runCatching {
        val parts = value.split(':')
        require(parts.size <= 2)
        val urlBuilder = HttpUrl.Builder()
        urlBuilder.host(parts.first())
        if (parts.size == 2) {
            urlBuilder.port(parts[1].toInt())
        }
    }.isSuccess
}

abstract class EditTextValidator : TextWatcher {

    private var editTextRef: WeakReference<EditText>? = null

    protected val context: Context
        get() = checkNotNull(editTextRef?.get()?.context) {
            "EditTextValidator is not attached to EditText"
        }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    @CallSuper
    override fun afterTextChanged(s: Editable?) {
        val editText = editTextRef?.get() ?: return
        val newText = s?.toString().orEmpty()
        val result = runCatching {
            validate(newText)
        }.getOrElse { e ->
            ValidationResult.Failed(e.getDisplayMessage(editText.resources))
        }
        editText.error = when (result) {
            is ValidationResult.Failed -> result.message
            ValidationResult.Success -> null
        }
    }

    fun attachToEditText(editText: EditText) {
        editTextRef = WeakReference(editText)
        editText.removeTextChangedListener(this)
        editText.addTextChangedListener(this)
        afterTextChanged(editText.text)
    }

    abstract fun validate(text: String): ValidationResult

    sealed class ValidationResult {

        object Success : ValidationResult()

        class Failed(val message: CharSequence) : ValidationResult()
    }
}