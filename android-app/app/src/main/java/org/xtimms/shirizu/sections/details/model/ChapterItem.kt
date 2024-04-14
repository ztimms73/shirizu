package org.xtimms.shirizu.sections.details.model

import android.text.format.DateUtils
import org.koitharu.kotatsu.parsers.model.MangaChapter
import org.xtimms.shirizu.core.model.ListModel
import org.xtimms.shirizu.core.model.formatNumber
import org.jsoup.internal.StringUtil.StringJoiner

data class ChapterItem(
    val chapter: MangaChapter,
    val flags: Int,
    private val uploadDateMs: Long,
) : ListModel {

    var description: String? = null
        private set
        get() {
            if (field != null) return field
            field = buildDescription()
            return field
        }

    var uploadDate: CharSequence? = null
        private set
        get() {
            if (field != null) return field
            if (uploadDateMs == 0L) return null
            field = DateUtils.getRelativeTimeSpanString(
                uploadDateMs,
                System.currentTimeMillis(),
                DateUtils.DAY_IN_MILLIS,
            )
            return field
        }

    val isCurrent: Boolean
        get() = hasFlag(FLAG_CURRENT)

    val isUnread: Boolean
        get() = hasFlag(FLAG_UNREAD)

    val isDownloaded: Boolean
        get() = hasFlag(FLAG_DOWNLOADED)

    val isBookmarked: Boolean
        get() = hasFlag(FLAG_BOOKMARKED)

    val isNew: Boolean
        get() = hasFlag(FLAG_NEW)

    private fun buildDescription(): String {
        val joiner = StringJoiner(" • ")
        chapter.formatNumber()?.let {
            joiner.add("#").append(it)
        }
        uploadDate?.let { date ->
            joiner.add(date.toString())
        }
        chapter.scanlator?.let { scanlator ->
            if (scanlator.isNotBlank()) {
                joiner.add(scanlator)
            }
        }
        return joiner.complete()
    }

    private fun hasFlag(flag: Int): Boolean {
        return (flags and flag) == flag
    }

    override fun areItemsTheSame(other: ListModel): Boolean {
        return other is ChapterItem && chapter.id == other.chapter.id
    }

    override fun getChangePayload(previousState: ListModel): Any? {
        if (previousState !is ChapterItem) {
            return super.getChangePayload(previousState)
        }
        return if (chapter == previousState.chapter && flags != previousState.flags) {
            flags
        } else {
            super.getChangePayload(previousState)
        }
    }

    companion object {

        const val FLAG_UNREAD = 2
        const val FLAG_CURRENT = 4
        const val FLAG_NEW = 8
        const val FLAG_BOOKMARKED = 16
        const val FLAG_DOWNLOADED = 32
    }
}