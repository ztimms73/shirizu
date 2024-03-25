package org.xtimms.tokusho.work

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.xtimms.tokusho.utils.lang.processLifecycleScope
import org.xtimms.tokusho.work.suggestions.SuggestionsWorker
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkScheduleManager @Inject constructor(
    private val suggestionScheduler: SuggestionsWorker.Scheduler,
) {

    fun init() {
        processLifecycleScope.launch(Dispatchers.Default) {
            updateWorkerImpl(suggestionScheduler, isEnabled = true, force = false)
        }
    }

    private suspend fun updateWorkerImpl(scheduler: PeriodicWorkScheduler, isEnabled: Boolean, force: Boolean) {
        if (force || scheduler.isScheduled() != isEnabled) {
            if (isEnabled) {
                scheduler.schedule()
            } else {
                scheduler.unschedule()
            }
        }
    }
}