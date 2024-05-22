package org.xtimms.shirizu.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration3To4 : Migration(3, 4) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
			CREATE TABLE IF NOT EXISTS `scrobblings` (
				`scrobbler` INTEGER NOT NULL,
				`id` INTEGER NOT NULL,
				`manga_id` INTEGER NOT NULL,
				`target_id` INTEGER NOT NULL, 
				`status` TEXT,
				`chapter` INTEGER NOT NULL, 
				`comment` TEXT,
				`rating` REAL NOT NULL,
				PRIMARY KEY(`scrobbler`, `id`, `manga_id`)
			)
			""".trimIndent()
        )
    }
}