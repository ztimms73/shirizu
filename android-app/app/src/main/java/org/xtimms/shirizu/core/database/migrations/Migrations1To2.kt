package org.xtimms.shirizu.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration1To2 : Migration(1, 2) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `stats` (`manga_id` INTEGER NOT NULL, `started_at` INTEGER NOT NULL, `duration` INTEGER NOT NULL, `pages` INTEGER NOT NULL, PRIMARY KEY(`manga_id`, `started_at`), FOREIGN KEY(`manga_id`) REFERENCES `history`(`manga_id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
    }
}