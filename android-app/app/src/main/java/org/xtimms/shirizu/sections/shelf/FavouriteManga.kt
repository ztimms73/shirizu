package org.xtimms.shirizu.sections.shelf

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import org.xtimms.shirizu.core.database.entity.FavouriteCategoryEntity
import org.xtimms.shirizu.core.database.entity.FavouriteEntity
import org.xtimms.shirizu.core.database.entity.MangaEntity
import org.xtimms.shirizu.core.database.entity.MangaTagsEntity
import org.xtimms.shirizu.core.database.entity.TagEntity

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