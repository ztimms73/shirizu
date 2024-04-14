package org.xtimms.shirizu.sections.stats.domain

import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.shirizu.core.model.ListModel
import org.xtimms.shirizu.sections.details.data.ReadingTime
import java.util.concurrent.TimeUnit

data class StatsRecord(
    val manga: Manga?,
    val duration: Long,
) : ListModel {

    override fun areItemsTheSame(other: ListModel): Boolean {
        return other is StatsRecord && other.manga == manga
    }

    val time: ReadingTime

    init {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration).toInt()
        time = ReadingTime(
            minutes = minutes % 60,
            hours = minutes / 60,
            isContinue = false,
        )
    }
}