package org.xtimms.shirizu.work

interface PeriodicWorkScheduler {

    suspend fun schedule()

    suspend fun unschedule()

    suspend fun isScheduled(): Boolean
}