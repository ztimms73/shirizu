package org.xtimms.etsudoku.utils.lang

import android.content.res.Resources
import org.xtimms.etsudoku.R
import java.text.DateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date

fun Date.toDateTimestampString(dateFormatter: DateFormat): String {
    val date = dateFormatter.format(this)
    val time = DateFormat.getTimeInstance(DateFormat.SHORT).format(this)
    return "$date $time"
}

fun Date.toTimestampString(): String {
    return DateFormat.getTimeInstance(DateFormat.SHORT).format(this)
}

fun calculateTimeAgo(instant: Instant, showMonths: Boolean = false): DateTimeAgo {
    // TODO: Use Java 9's LocalDate.ofInstant().
    val localDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate()
    val now = LocalDate.now()
    val diffDays = localDate.until(now, ChronoUnit.DAYS)

    return when {
        diffDays == 0L -> DateTimeAgo.Today
        diffDays == 1L -> DateTimeAgo.Yesterday
        diffDays < 6 -> DateTimeAgo.DaysAgo(diffDays.toInt())
        else -> {
            val diffMonths = localDate.until(now, ChronoUnit.MONTHS)
            if (showMonths && diffMonths <= 6) {
                DateTimeAgo.MonthsAgo(diffMonths.toInt())
            } else {
                DateTimeAgo.Absolute(localDate)
            }
        }
    }
}

fun isSameDay(timestampA: Long, timestampB: Long): Boolean {
    return isSameDay(Date(timestampA), Date(timestampB))
}

fun isSameDay(dateA: Date, dateB: Date): Boolean {
    return roundToDay(dateA) == roundToDay(dateB)
}

fun roundToDay(date: Date): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date

    return Calendar
        .Builder()
        .setTimeZone(calendar.timeZone)
        .setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        .build()
        .time
}

fun LocalDate.toDate(): Date = Date(this.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000)

fun LocalDateTime.toDate(): Date = Date(this.toEpochSecond(
    ZoneId.systemDefault().rules.getOffset(this)
) * 1000)

sealed class DateTimeAgo {

    abstract fun format(resources: Resources): String

    object JustNow : DateTimeAgo() {
        override fun format(resources: Resources): String {
            return resources.getString(R.string.just_now)
        }

        override fun toString() = "just_now"

        override fun equals(other: Any?): Boolean = other === JustNow
    }

    data class MinutesAgo(val minutes: Int) : DateTimeAgo() {
        override fun format(resources: Resources): String {
            return resources.getQuantityString(R.plurals.minutes_ago, minutes, minutes)
        }

        override fun toString() = "minutes_ago_$minutes"
    }

    data class HoursAgo(val hours: Int) : DateTimeAgo() {
        override fun format(resources: Resources): String {
            return resources.getQuantityString(R.plurals.hours_ago, hours, hours)
        }

        override fun toString() = "hours_ago_$hours"
    }

    object Today : DateTimeAgo() {
        override fun format(resources: Resources): String {
            return resources.getString(R.string.today)
        }

        override fun toString() = "today"

        override fun equals(other: Any?): Boolean = other === Today
    }

    object Yesterday : DateTimeAgo() {
        override fun format(resources: Resources): String {
            return resources.getString(R.string.yesterday)
        }

        override fun toString() = "yesterday"

        override fun equals(other: Any?): Boolean = other === Yesterday
    }

    data class DaysAgo(val days: Int) : DateTimeAgo() {
        override fun format(resources: Resources): String {
            return resources.getQuantityString(R.plurals.days_ago, days, days)
        }

        override fun toString() = "days_ago_$days"
    }

    data class MonthsAgo(val months: Int) : DateTimeAgo() {
        override fun format(resources: Resources): String {
            return if (months == 0) {
                resources.getString(R.string.this_month)
            } else {
                resources.getQuantityString(R.plurals.months_ago, months, months)
            }
        }
    }

    data class Absolute(private val date: LocalDate) : DateTimeAgo() {
        override fun format(resources: Resources): String {
            return if (date == EPOCH_DATE) {
                resources.getString(R.string.unknown)
            } else {
                date.format(formatter)
            }
        }

        override fun toString() = "abs_${date.toEpochDay()}"

        companion object {
            // TODO: Use Java 9's LocalDate.EPOCH.
            private val EPOCH_DATE = LocalDate.of(1970, 1, 1)
            private val formatter = DateTimeFormatter.ofPattern("d MMMM")
        }
    }

    object LongAgo : DateTimeAgo() {
        override fun format(resources: Resources): String {
            return resources.getString(R.string.long_ago)
        }

        override fun toString() = "long_ago"

        override fun equals(other: Any?): Boolean = other === LongAgo
    }
}