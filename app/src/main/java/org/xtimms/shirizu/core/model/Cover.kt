package org.xtimms.shirizu.core.model

data class Cover(
    val url: String?,
    val source: String,
) {
    val mangaSource by lazy { MangaSource(source) }
}