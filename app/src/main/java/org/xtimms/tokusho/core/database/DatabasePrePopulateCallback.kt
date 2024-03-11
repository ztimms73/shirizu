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
                "https://uploads.mangadex.org/covers/822c9883-385c-4fd0-9523-16e7789cbeae/542f379f-adee-4d27-bdd1-ffd81b140851.jpg.256.jpg",
                "https://uploads.mangadex.org/covers/822c9883-385c-4fd0-9523-16e7789cbeae/542f379f-adee-4d27-bdd1-ffd81b140851.jpg",
                "FINISHED",
                "Muchi Maro",
                "MANGADEX",
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
    }
}