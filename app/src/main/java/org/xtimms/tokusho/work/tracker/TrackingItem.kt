package org.xtimms.tokusho.work.tracker

import org.xtimms.tokusho.core.tracker.model.MangaTracking

data class TrackingItem(
    val tracking: MangaTracking,
    val channelId: String?,
)