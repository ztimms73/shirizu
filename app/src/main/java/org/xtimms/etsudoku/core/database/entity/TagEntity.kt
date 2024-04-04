package org.xtimms.etsudoku.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.xtimms.etsudoku.core.database.TABLE_TAGS

@Entity(tableName = TABLE_TAGS)
data class TagEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "tag_id") val id: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "key") val key: String,
    @ColumnInfo(name = "source") val source: String,
)