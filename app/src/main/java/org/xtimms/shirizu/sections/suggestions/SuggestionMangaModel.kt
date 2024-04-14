package org.xtimms.shirizu.sections.suggestions

import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.shirizu.core.model.ListModel

data class SuggestionMangaModel(
    val manga: Manga
) : ListModel {

    override fun areItemsTheSame(other: ListModel): Boolean {
        return other is SuggestionMangaModel && other.manga.id == manga.id
    }
}