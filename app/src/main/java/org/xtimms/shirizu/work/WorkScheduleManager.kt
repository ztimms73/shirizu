package org.xtimms.shirizu.work

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.xtimms.shirizu.core.prefs.AppSettings
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
            updateWorkerImpl(trackerScheduler, AppSettings.isTrackerEnabled(), force = true)
            updateWorkerImpl(suggestionScheduler, AppSettings.isSuggestionsEnabled(), force = false)
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