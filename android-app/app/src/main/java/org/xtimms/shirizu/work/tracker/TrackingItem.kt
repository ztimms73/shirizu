package org.xtimms.shirizu.work.tracker

import org.xtimms.shirizu.core.tracker.model.MangaTracking

data class TrackingItem(
    val tracking: MangaTracking,
    val channelId: String?,
)