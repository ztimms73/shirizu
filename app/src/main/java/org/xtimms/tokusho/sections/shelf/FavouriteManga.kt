package org.xtimms.tokusho.sections.shelf

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import org.xtimms.tokusho.core.database.entity.FavouriteCategoryEntity
import org.xtimms.tokusho.core.database.entity.FavouriteEntity
import org.xtimms.tokusho.core.database.entity.MangaEntity
import org.xtimms.tokusho.core.database.entity.MangaTagsEntity
import org.xtimms.tokusho.core.database.entity.TagEntity

class FavouriteManga(
    @Embedded val favourite: FavouriteEntity,
    @Relation(
        parentColumn = "manga_id",
        entityColumn = "manga_id"
    )
    val manga: MangaEntity,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "category_id"
    )
    val categories: List<FavouriteCategoryEntity>,
    @Relation(
        parentColumn = "manga_id",
        entityColumn = "tag_id",
        associateBy = Junction(MangaTagsEntity::class)
    )
    val tags: List<TagEntity>
)