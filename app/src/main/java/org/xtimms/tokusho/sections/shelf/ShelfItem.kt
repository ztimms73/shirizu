package org.xtimms.tokusho.sections.shelf

data class ShelfItem(
    val libraryManga: ShelfManga,
    val downloadCount: Long = -1,
    val unreadCount: Long = -1,
    val isLocal: Boolean = false,
    val sourceLanguage: String = "",
)