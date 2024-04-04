package org.xtimms.etsudoku.core.exceptions

class SyncApiException(
    message: String,
    val code: Int,
) : RuntimeException(message)