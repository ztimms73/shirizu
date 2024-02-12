package org.xtimms.tokusho.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.xtimms.tokusho.core.database.TABLE_HISTORY

@Entity(
    tableName = TABLE_HISTORY,
    foreignKeys = [
        ForeignKey(
            entity = MangaEntity::class,
            parentColumns = ["manga_id"],
            childColumns = ["manga_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class HistoryEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "manga_id") val mangaId: Long,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
    @ColumnInfo(name = "chapter_id") val chapterId: Long,
    @ColumnInfo(name = "page") val page: Int,
    @ColumnInfo(name = "scroll") val scroll: Float,
    @ColumnInfo(name = "percent") val percent: Float,
    @ColumnInfo(name = "deleted_at") val deletedAt: Long,
)