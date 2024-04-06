package org.xtimms.shirizu.core.database.entity

import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaState
import org.koitharu.kotatsu.parsers.model.MangaTag
import org.koitharu.kotatsu.parsers.model.SortOrder
import org.koitharu.kotatsu.parsers.util.mapToSet
import org.koitharu.kotatsu.parsers.util.toTitleCase
import org.xtimms.shirizu.core.model.Bookmark
import org.xtimms.shirizu.core.model.FavouriteCategory
import org.xtimms.shirizu.core.model.ListSortOrder
import org.xtimms.shirizu.core.model.MangaHistory
import org.xtimms.shirizu.core.model.MangaSource
import org.xtimms.shirizu.core.tracker.model.TrackingLogItem
import org.xtimms.shirizu.sections.shelf.FavouriteManga
import org.xtimms.shirizu.utils.lang.longHashCode
import java.time.Instant

// Entity to model

fun TagEntity.toMangaTag() = MangaTag(
    key = this.key,
    title = this.title.toTitleCase(),
    source = MangaSource(this.source),
)

fun Collection<TagEntity>.toMangaTags() = mapToSet(TagEntity::toMangaTag)

fun Collection<TagEntity>.toMangaTagsList() = map(TagEntity::toMangaTag)

fun MangaEntity.toManga(tags: Set<MangaTag>) = Manga(
    id = this.id,
    title = this.title,
    altTitle = this.altTitle,
    state = this.state?.let { MangaState(it) },
    rating = this.rating,
    isNsfw = this.isNsfw,
    url = this.url,
    publicUrl = this.publicUrl,
    coverUrl = this.coverUrl,
    largeCoverUrl = this.largeCoverUrl,
    author = this.author,
    source = MangaSource(this.source),
    tags = tags,
)

fun MangaWithTags.toManga() = manga.toManga(tags.toMangaTags())

fun FavouriteCategoryEntity.toFavouriteCategory(id: Long = categoryId.toLong()) = FavouriteCategory(
    id = id,
    title = title,
    sortKey = sortKey,
    order = ListSortOrder(order, ListSortOrder.NEWEST),
    createdAt = Instant.ofEpochMilli(createdAt),
    isTrackingEnabled = track,
    isVisibleInLibrary = isVisibleInLibrary,
)

fun FavouriteManga.toManga() = manga.toManga(tags.toMangaTags())

fun Collection<FavouriteManga>.toMangaList() = map { it.toManga() }

fun BookmarkEntity.toBookmark(manga: Manga) = Bookmark(
    manga = manga,
    pageId = pageId,
    chapterId = chapterId,
    page = page,
    scroll = scroll,
    imageUrl = imageUrl,
    createdAt = Instant.ofEpochMilli(createdAt),
    percent = percent,
)

fun Collection<BookmarkEntity>.toBookmarks(manga: Manga) = map {
    it.toBookmark(manga)
}

@JvmName("bookmarksIds")
fun Collection<Bookmark>.ids() = map { it.pageId }

fun TrackLogWithManga.toTrackingLogItem(counters: MutableMap<Long, Int>): TrackingLogItem {
    val chaptersList = trackLog.chapters.split('\n').filterNot { x -> x.isEmpty() }
    return TrackingLogItem(
        id = trackLog.id,
        chapters = chaptersList,
        manga = manga.toManga(tags.toMangaTags()),
        createdAt = Instant.ofEpochMilli(trackLog.createdAt),
        isNew = counters.decrement(trackLog.mangaId, chaptersList.size),
    )
}

private fun MutableMap<Long, Int>.decrement(key: Long, count: Int): Boolean = synchronized(this) {
    val counter = get(key)
    if (counter == null || counter <= 0) {
        return false
    }
    if (counter < count) {
        remove(key)
    } else {
        put(key, counter - count)
    }
    return true
}

// Model to entity

fun Manga.toEntity() = MangaEntity(
    id = id,
    url = url,
    publicUrl = publicUrl,
    source = source.name,
    largeCoverUrl = largeCoverUrl,
    coverUrl = coverUrl,
    altTitle = altTitle,
    rating = rating,
    isNsfw = isNsfw,
    state = state?.name,
    title = title,
    author = author,
)

fun MangaTag.toEntity() = TagEntity(
    title = title,
    key = key,
    source = source.name,
    id = "${key}_${source.name}".longHashCode(),
)

fun Collection<MangaTag>.toEntities() = map(MangaTag::toEntity)

fun Bookmark.toEntity() = BookmarkEntity(
    mangaId = manga.id,
    pageId = pageId,
    chapterId = chapterId,
    page = page,
    scroll = scroll,
    imageUrl = imageUrl,
    createdAt = createdAt.toEpochMilli(),
    percent = percent,
)

// Other

fun SortOrder(name: String, fallback: SortOrder): SortOrder = runCatching {
    SortOrder.valueOf(name)
}.getOrDefault(fallback)

fun MangaState(name: String): MangaState? = runCatching {
    MangaState.valueOf(name)
}.getOrNull()

fun HistoryEntity.toMangaHistory() = MangaHistory(
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt),
    chapterId = chapterId,
    page = page,
    scroll = scroll.toInt(),
    percent = percent,
)