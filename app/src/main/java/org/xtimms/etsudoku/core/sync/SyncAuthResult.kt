package org.xtimms.etsudoku.core.sync

data class SyncAuthResult(
    val host: String,
    val email: String,
    val password: String,
    val token: String,
)