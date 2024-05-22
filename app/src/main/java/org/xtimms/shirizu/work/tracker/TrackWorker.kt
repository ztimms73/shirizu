package org.xtimms.shirizu.work.tracker

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationCompat.VISIBILITY_SECRET
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import androidx.work.WorkerParameters
import androidx.work.await
import coil.ImageLoader
import coil.request.ImageRequest
import dagger.Reusable
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaChapter
import org.koitharu.kotatsu.parsers.util.runCatchingCancellable
import org.koitharu.kotatsu.parsers.util.toIntUp
import org.xtimms.shirizu.BuildConfig
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.database.ShirizuDatabase
import org.xtimms.shirizu.core.exceptions.CloudflareProtectedException
import org.xtimms.shirizu.core.logs.FileLogger
import org.xtimms.shirizu.core.logs.TrackerLogger
import org.xtimms.shirizu.core.prefs.AppSettings
import org.xtimms.shirizu.core.tracker.Tracker
import org.xtimms.shirizu.core.tracker.model.MangaUpdates
import org.xtimms.shirizu.utils.lang.awaitUniqueWorkInfoByName
import org.xtimms.shirizu.utils.lang.onEachIndexed
import org.xtimms.shirizu.utils.lang.toBitmapOrNull
import org.xtimms.shirizu.utils.system.checkNotificationPermission
import org.xtimms.shirizu.utils.system.trySetForeground
import org.xtimms.shirizu.work.PeriodicWorkScheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider

@HiltWorker
class TrackWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val coil: ImageLoader,
    private val tracker: Tracker,
    private val workManager: WorkManager,
    @TrackerLogger private val logger: FileLogger,
) : CoroutineWorker(context, workerParams) {

    private val notificationManager by lazy { NotificationManagerCompat.from(applicationContext) }

    override suspend fun doWork(): Result {
        val isForeground = trySetForeground()
        logger.log("doWork(): attempt $runAttemptCount")
        return try {
            doWorkImpl(isFullRun = isForeground && TAG_ONESHOT in tags)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            logger.log("fatal", e)
            Result.failure()
        } finally {
            withContext(NonCancellable) {
                logger.flush()
                notificationManager.cancel(WORKER_NOTIFICATION_ID)
            }
        }
    }

    private suspend fun doWorkImpl(isFullRun: Boolean): Result {
        if (!AppSettings.isTrackerEnabled()) {
            return Result.success(workDataOf(0, 0))
        }
        val tracks = tracker.getTracks(if (isFullRun) Int.MAX_VALUE else BATCH_SIZE)
        logger.log("Total ${tracks.size} tracks")
        if (tracks.isEmpty()) {
            return Result.success(workDataOf(0, 0))
        }

        checkUpdatesAsync(tracks)
        return Result.success()
    }

    private suspend fun checkUpdatesAsync(tracks: List<TrackingItem>): List<MangaUpdates> {
        val semaphore = Semaphore(MAX_PARALLELISM)
        return channelFlow {
            for ((track, channelId) in tracks) {
                launch {
                    semaphore.withPermit {
                        send(
                            runCatchingCancellable {
                                tracker.fetchUpdates(track, commit = true).let {
                                    if (it is MangaUpdates.Success) {
                                        it.copy(channelId = channelId)
                                    } else {
                                        it
                                    }
                                }
                            }.getOrElse { error ->
                                MangaUpdates.Failure(
                                    manga = track.manga,
                                    error = error,
                                )
                            },
                        )
                    }
                }
            }
        }.onEachIndexed { index, it ->
            if (applicationContext.checkNotificationPermission()) {
                notificationManager.notify(WORKER_NOTIFICATION_ID, createWorkerNotification(tracks.size, index + 1))
            }
            when (it) {
                is MangaUpdates.Failure -> {
                    val e = it.error
                    logger.log("checkUpdatesAsync", e)
                    if (e is CloudflareProtectedException) {
                        e.printStackTrace()
                    }
                }

                is MangaUpdates.Success -> {
                    if (it.isValid && it.isNotEmpty()) {
                        showNotification(
                            manga = it.manga,
                            channelId = it.channelId,
                            newChapters = it.newChapters,
                        )
                    }
                }
            }
        }.toList(ArrayList(tracks.size))
    }

    private suspend fun showNotification(
        manga: Manga,
        channelId: String?,
        newChapters: List<MangaChapter>,
    ) {
        if (newChapters.isEmpty() || channelId == null || !applicationContext.checkNotificationPermission()) {
            return
        }
        val id = manga.url.hashCode()
        val builder = NotificationCompat.Builder(applicationContext, channelId)
        val summary = applicationContext.resources.getQuantityString(
            R.plurals.new_chapters,
            newChapters.size,
            newChapters.size,
        )
        with(builder) {
            setContentText(summary)
            setContentTitle(manga.title)
            setNumber(newChapters.size)
            setLargeIcon(
                coil.execute(
                    ImageRequest.Builder(applicationContext)
                        .data(manga.coverUrl)
                        .tag(manga.source)
                        .build(),
                ).toBitmapOrNull(),
            )
            setSmallIcon(R.drawable.ic_stat_shirizu)
            val style = NotificationCompat.InboxStyle(this)
            for (chapter in newChapters) {
                style.addLine(chapter.name)
            }
            style.setSummaryText(manga.title)
            style.setBigContentTitle(summary)
            setStyle(style)
            setAutoCancel(true)
            setCategory(NotificationCompat.CATEGORY_PROMO)
            setVisibility(if (manga.isNsfw) VISIBILITY_SECRET else VISIBILITY_PUBLIC)
            setShortcutId(manga.id.toString())
            priority = NotificationCompat.PRIORITY_DEFAULT
        }
        notificationManager.notify(TAG, id, builder.build())
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val channel = NotificationChannelCompat.Builder(
            WORKER_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_LOW,
        )
            .setName(applicationContext.getString(R.string.check_for_new_chapters))
            .setShowBadge(false)
            .setVibrationEnabled(false)
            .setSound(null, null)
            .setLightsEnabled(false)
            .build()
        notificationManager.createNotificationChannel(channel)

        val notification = createWorkerNotification(0, 0)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                WORKER_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
            )
        } else {
            ForegroundInfo(WORKER_NOTIFICATION_ID, notification)
        }
    }

    private fun createWorkerNotification(max: Int, progress: Int) = NotificationCompat.Builder(
        applicationContext,
        WORKER_CHANNEL_ID,
    ).apply {
        setContentTitle(applicationContext.getString(R.string.check_for_new_chapters))
        priority = NotificationCompat.PRIORITY_MIN
        setCategory(NotificationCompat.CATEGORY_SERVICE)
        setDefaults(0)
        setOngoing(false)
        setOnlyAlertOnce(true)
        setSilent(true)
        addAction(
            com.google.android.material.R.drawable.material_ic_clear_black_24dp,
            applicationContext.getString(android.R.string.cancel),
            workManager.createCancelPendingIntent(id),
        )
        if (max > 0) {
            setSubText(applicationContext.getString(R.string.fraction_pattern, progress, max))
        }
        setProgress(max, progress, max == 0)
        setSmallIcon(android.R.drawable.stat_notify_sync)
        foregroundServiceBehavior = if (TAG_ONESHOT in tags) {
            NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
        } else {
            NotificationCompat.FOREGROUND_SERVICE_DEFERRED
        }
    }.build()

    private fun workDataOf(success: Int, failed: Int): Data {
        return Data.Builder()
            .putInt(DATA_KEY_SUCCESS, success)
            .putInt(DATA_KEY_FAILED, failed)
            .build()
    }

    @Reusable
    class Scheduler @Inject constructor(
        private val workManager: WorkManager,
        private val dbProvider: Provider<ShirizuDatabase>,
    ) : PeriodicWorkScheduler {

        override suspend fun schedule() {
            val constraints = createConstraints()
            val runCount = dbProvider.get().getTracksDao().getTracksCount()
            val runsPerFullCheck = (runCount / BATCH_SIZE.toFloat()).toIntUp().coerceAtLeast(1)
            val interval = (6 / runsPerFullCheck).coerceAtLeast(2)
            val request = PeriodicWorkRequestBuilder<TrackWorker>(interval.toLong(), TimeUnit.HOURS)
                .setConstraints(constraints)
                .addTag(TAG)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.MINUTES)
                .build()
            workManager
                .enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.UPDATE, request)
                .await()
        }

        override suspend fun unschedule() {
            workManager
                .cancelUniqueWork(TAG)
                .await()
        }

        override suspend fun isScheduled(): Boolean {
            return workManager
                .awaitUniqueWorkInfoByName(TAG)
                .any { !it.state.isFinished }
        }

        fun startNow() {
            val constraints =
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            val request = OneTimeWorkRequestBuilder<TrackWorker>()
                .setConstraints(constraints)
                .addTag(TAG_ONESHOT)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
            workManager.enqueue(request)
        }

        fun observeIsRunning(): Flow<Boolean> {
            val query = WorkQuery.Builder.fromTags(listOf(TAG, TAG_ONESHOT)).build()
            return workManager.getWorkInfosFlow(query)
                .map { works ->
                    works.any { x -> x.state == WorkInfo.State.RUNNING }
                }
        }

        private fun createConstraints() = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }

    private companion object {

        const val WORKER_CHANNEL_ID = "track_worker"
        const val WORKER_NOTIFICATION_ID = 35
        const val TAG = "tracking"
        const val TAG_ONESHOT = "tracking_oneshot"
        const val MAX_PARALLELISM = 6
        const val DATA_KEY_SUCCESS = "success"
        const val DATA_KEY_FAILED = "failed"
        val BATCH_SIZE = if (BuildConfig.DEBUG) 20 else 46
    }
}