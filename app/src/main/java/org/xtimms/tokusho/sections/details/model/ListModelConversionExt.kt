package org.xtimms.tokusho.sections.details.model

import org.koitharu.kotatsu.parsers.model.MangaChapter
import org.xtimms.tokusho.sections.details.model.ChapterItem.Companion.FLAG_BOOKMARKED
import org.xtimms.tokusho.sections.details.model.ChapterItem.Companion.FLAG_CURRENT
import org.xtimms.tokusho.sections.details.model.ChapterItem.Companion.FLAG_DOWNLOADED
import org.xtimms.tokusho.sections.details.model.ChapterItem.Companion.FLAG_NEW
import org.xtimms.tokusho.sections.details.model.ChapterItem.Companion.FLAG_UNREAD

fun MangaChapter.toListItem(
    isCurrent: Boolean,
    isUnread: Boolean,
    isNew: Boolean,
    isDownloaded: Boolean,
    isBookmarked: Boolean,
): ChapterItem {
    var flags = 0
    if (isCurrent) flags = flags or FLAG_CURRENT
    if (isUnread) flags = flags or FLAG_UNREAD
    if (isNew) flags = flags or FLAG_NEW
    if (isBookmarked) flags = flags or FLAG_BOOKMARKED
    if (isDownloaded) flags = flags or FLAG_DOWNLOADED
    return ChapterItem(
        chapter = this,
        flags = flags,
        uploadDateMs = uploadDate,
    )
}