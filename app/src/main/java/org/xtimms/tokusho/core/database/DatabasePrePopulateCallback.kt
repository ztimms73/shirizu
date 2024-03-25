package org.xtimms.tokusho.core.database

import android.content.res.Resources
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import org.koitharu.kotatsu.parsers.model.SortOrder
import org.xtimms.tokusho.R

class DatabasePrePopulateCallback(private val resources: Resources) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "INSERT INTO favourite_categories (created_at, sort_key, title, `order`, track, show_in_lib, `deleted_at`) VALUES (?,?,?,?,?,?,?)",
            arrayOf(
                System.currentTimeMillis(),
                1,
                resources.getString(R.string.read_later),
                SortOrder.NEWEST.name,
                1,
                1,
                0L,
            )
        )
        db.execSQL(
            "INSERT INTO favourite_categories (created_at, sort_key, title, `order`, track, show_in_lib, `deleted_at`) VALUES (?,?,?,?,?,?,?)",
            arrayOf(
                System.currentTimeMillis(),
                1,
                resources.getString(R.string.reading),
                SortOrder.NEWEST.name,
                1,
                1,
                0L,
            )
        )
        db.execSQL(
            "INSERT INTO favourite_categories (created_at, sort_key, title, `order`, track, show_in_lib, `deleted_at`) VALUES (?,?,?,?,?,?,?)",
            arrayOf(
                System.currentTimeMillis(),
                1,
                resources.getString(R.string.completed),
                SortOrder.NEWEST.name,
                1,
                1,
                0L,
            )
        )
        db.execSQL(
            "INSERT INTO favourite_categories (created_at, sort_key, title, `order`, track, show_in_lib, `deleted_at`) VALUES (?,?,?,?,?,?,?)",
            arrayOf(
                System.currentTimeMillis(),
                1,
                resources.getString(R.string.dropped),
                SortOrder.NEWEST.name,
                1,
                1,
                0L,
            )
        )
        db.execSQL(
            "INSERT INTO sources (source, enabled, sort_key) VALUES (?,?,?)",
            arrayOf(
                "MANGADEX",
                1,
                1,
            )
        )
        db.execSQL(
            "INSERT INTO sources (source, enabled, sort_key) VALUES (?,?,?)",
            arrayOf(
                "DESUME",
                1,
                1,
            )
        )
        db.execSQL(
            "INSERT INTO manga (manga_id, title, alt_title, url, public_url, rating, nsfw, cover_url, large_cover_url, state, author, source) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
            arrayOf(
                4427365311541330000,
                "Seitokai ni mo Ana wa Aru!",
                "",
                "822c9883-385c-4fd0-9523-16e7789cbeae",
                "https://mangadex.org/title/822c9883-385c-4fd0-9523-16e7789cbeae",
                -1.0,
                0,
                "https://mangadex.org/covers/822c9883-385c-4fd0-9523-16e7789cbeae/f886822a-80c3-484c-ad75-9aa32abedc18.jpg.256.jpg",
                "https://mangadex.org/covers/822c9883-385c-4fd0-9523-16e7789cbeae/f886822a-80c3-484c-ad75-9aa32abedc18.jpg",
                "FINISHED",
                "Muchi Maro",
                "MANGADEX",
            )
        )
        db.execSQL(
            "INSERT INTO manga (manga_id, title, alt_title, url, public_url, rating, nsfw, cover_url, large_cover_url, state, author, source) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
            arrayOf(
                -5513532524243987690,
                "Тотальный гарем",
                "Shuumatsu no Harem",
                "/manga/api/694",
                "https://desu.me/manga/z-shuumatsu-no-harem.694/",
                1.0,
                1,
                "https://desu.me/data/manga/covers/preview/694.jpg",
                "https://desu.me/data/manga/covers/original/694.jpg",
                "ONGOING",
                "",
                "DESUME",
            )
        )
        db.execSQL(
            "INSERT INTO favourites (manga_id, category_id, sort_key, created_at, deleted_at) VALUES (?,?,?,?,?)",
            arrayOf(
                4427365311541330000,
                1,
                0,
                1705944302882,
                0,
            )
        )
        db.execSQL(
            "INSERT INTO favourites (manga_id, category_id, sort_key, created_at, deleted_at) VALUES (?,?,?,?,?)",
            arrayOf(
                -5513532524243987690,
                1,
                0,
                1705944302882,
                0,
            )
        )
        db.execSQL(
            "INSERT into history (manga_id, created_at, updated_at, chapter_id, page, scroll, percent, deleted_at) VALUES (?,?,?,?,?,?,?,?)",
            arrayOf(
                -5513532524243987690,
                1710617414,
                1710617414,
                1,
                3,
                0.3,
                0.4,
                0
            )
        )
    }
}