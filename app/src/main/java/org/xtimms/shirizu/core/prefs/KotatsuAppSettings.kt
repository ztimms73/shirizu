package org.xtimms.shirizu.core.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import org.xtimms.shirizu.sections.explore.data.SourcesSortOrder
import org.xtimms.shirizu.utils.system.getEnumValue
import org.xtimms.shirizu.utils.system.putEnumValue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KotatsuAppSettings @Inject constructor(@ApplicationContext context: Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    var isNsfwContentDisabled: Boolean
        get() = prefs.getBoolean(KEY_DISABLE_NSFW, false)
        set(value) = prefs.edit { putBoolean(KEY_DISABLE_NSFW, value) }

    val isNewSourcesTipEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOURCES_NEW, true)

    var sourcesSortOrder: SourcesSortOrder
        get() = prefs.getEnumValue(KEY_SOURCES_ORDER, SourcesSortOrder.MANUAL)
        set(value) = prefs.edit { putEnumValue(KEY_SOURCES_ORDER, value) }

    fun subscribe(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unsubscribe(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

    fun observe() = prefs.observe()

    companion object {
        const val KEY_DISABLE_NSFW = "no_nsfw"
        const val KEY_SOURCES_NEW = "sources_new"
        const val KEY_SOURCES_ORDER = "sources_sort_order"
    }
}

fun <T> KotatsuAppSettings.observeAsFlow(key: String, valueProducer: KotatsuAppSettings.() -> T) = flow {
    var lastValue: T = valueProducer()
    emit(lastValue)
    observe().collect {
        if (it == key) {
            val value = valueProducer()
            if (value != lastValue) {
                emit(value)
            }
            lastValue = value
        }
    }
}

fun <T> KotatsuAppSettings.observeAsStateFlow(
    scope: CoroutineScope,
    key: String,
    valueProducer: KotatsuAppSettings.() -> T,
): StateFlow<T> = observe().transform {
    if (it == key) {
        emit(valueProducer())
    }
}.stateIn(scope, SharingStarted.Eagerly, valueProducer())

fun SharedPreferences.observe(): Flow<String?> = callbackFlow {
    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        trySendBlocking(key)
    }
    registerOnSharedPreferenceChangeListener(listener)
    awaitClose {
        unregisterOnSharedPreferenceChangeListener(listener)
    }
}