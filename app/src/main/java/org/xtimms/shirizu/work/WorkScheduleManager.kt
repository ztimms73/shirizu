package org.xtimms.shirizu.work

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.xtimms.shirizu.utils.lang.processLifecycleScope
import org.xtimms.shirizu.work.suggestions.SuggestionsWorker
import org.xtimms.shirizu.work.tracker.TrackWorker
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkScheduleManager @Inject constructor(
    private val suggestionScheduler: SuggestionsWorker.Scheduler,
    private val trackerScheduler: TrackWorker.Scheduler,
) {

    fun init() {
        processLifecycleScope.launch(Dispatchers.Default) {
            updateWorkerImpl(trackerScheduler, isEnabled = true, force = false) // TODO
            updateWorkerImpl(suggestionScheduler, isEnabled = true, force = false) // TODO
        }
    }

    private fun updateWorker(scheduler: PeriodicWorkScheduler, isEnabled: Boolean, force: Boolean) {
        processLifecycleScope.launch(Dispatchers.Default) {
            updateWorkerImpl(scheduler, isEnabled, force)
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