package org.xtimms.tokusho.core.logs

import android.content.Context
import androidx.collection.arraySetOf
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet

@Module
@InstallIn(SingletonComponent::class)
object LoggersModule {

    @Provides
    @TrackerLogger
    fun provideTrackerLogger(
        @ApplicationContext context: Context,
    ) = FileLogger(context, "tracker")

    @Provides
    @SyncLogger
    fun provideSyncLogger(
        @ApplicationContext context: Context,
    ) = FileLogger(context, "sync")

    @Provides
    @ElementsIntoSet
    fun provideAllLoggers(
        @TrackerLogger trackerLogger: FileLogger,
        @SyncLogger syncLogger: FileLogger,
    ): Set<@JvmSuppressWildcards FileLogger> = arraySetOf(
        trackerLogger,
        syncLogger,
    )
}