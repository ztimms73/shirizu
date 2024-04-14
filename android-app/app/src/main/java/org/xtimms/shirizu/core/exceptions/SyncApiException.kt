package org.xtimms.shirizu.core.exceptions

class SyncApiException(
    message: String,
    val code: Int,
) : RuntimeException(message)