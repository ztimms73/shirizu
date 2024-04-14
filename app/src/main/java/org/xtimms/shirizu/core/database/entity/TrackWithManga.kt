package org.xtimms.shirizu.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

class TrackWithManga(
    @Embedded val track: TrackEntity,
    @Relation(
        parentColumn = "manga_id",
        entityColumn = "manga_id",
    )
    val manga: MangaEntity,
)