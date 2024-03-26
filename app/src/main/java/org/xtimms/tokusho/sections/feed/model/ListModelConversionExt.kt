package org.xtimms.tokusho.sections.feed.model

import org.xtimms.tokusho.core.tracker.model.TrackingLogItem

fun TrackingLogItem.toFeedItem() = FeedItem(
    id = id,
    imageUrl = manga.coverUrl,
    title = manga.title,
    count = chapters.size,
    manga = manga,
    isNew = isNew,
)