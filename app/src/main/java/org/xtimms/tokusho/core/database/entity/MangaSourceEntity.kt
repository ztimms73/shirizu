package org.xtimms.tokusho.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.xtimms.tokusho.core.database.TABLE_SOURCES

@Entity(
    tableName = TABLE_SOURCES,
)
data class MangaSourceEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "source")
    val source: String,
    @ColumnInfo(name = "enabled") val isEnabled: Boolean,
    @ColumnInfo(name = "sort_key", index = true) val sortKey: Int,
)