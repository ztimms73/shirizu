package org.xtimms.etsudoku.work

interface PeriodicWorkScheduler {

    suspend fun schedule()

    suspend fun unschedule()

    suspend fun isScheduled(): Boolean
}