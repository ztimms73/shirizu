package org.xtimms.etsudoku.sections.feed.model

import org.xtimms.etsudoku.core.tracker.model.TrackingLogItem

fun TrackingLogItem.toFeedItem() = FeedItem(
    id = id,
    imageUrl = manga.coverUrl,
    title = manga.title,
    count = chapters.size,
    manga = manga,
    isNew = isNew,
)