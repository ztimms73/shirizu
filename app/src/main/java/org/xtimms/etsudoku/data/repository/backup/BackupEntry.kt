package org.xtimms.etsudoku.data.repository.backup

import org.json.JSONArray

class BackupEntry(
    val name: Name,
    val data: JSONArray
) {

    enum class Name(
        val key: String,
    ) {

        INDEX("index"),
        HISTORY("history"),
        CATEGORIES("categories"),
        FAVOURITES("favourites"),
        BOOKMARKS("bookmarks"),
        SOURCES("sources"),
    }
}
