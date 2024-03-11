package org.xtimms.tokusho.work

interface PeriodicWorkScheduler {

    suspend fun schedule()

    suspend fun unschedule()

    suspend fun isScheduled(): Boolean
}