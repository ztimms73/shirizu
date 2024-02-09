package org.xtimms.tokusho.utils.lang

import java.text.DateFormat
import java.util.Date

fun Date.toDateTimestampString(dateFormatter: DateFormat): String {
    val date = dateFormatter.format(this)
    val time = DateFormat.getTimeInstance(DateFormat.SHORT).format(this)
    return "$date $time"
}