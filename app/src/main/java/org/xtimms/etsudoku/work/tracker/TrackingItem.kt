package org.xtimms.etsudoku.work.tracker

import org.xtimms.etsudoku.core.tracker.model.MangaTracking

data class TrackingItem(
    val tracking: MangaTracking,
    val channelId: String?,
)