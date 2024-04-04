package org.xtimms.etsudoku.sections.suggestions

import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.etsudoku.core.model.ListModel

data class SuggestionMangaModel(
    val manga: Manga
) : ListModel {

    override fun areItemsTheSame(other: ListModel): Boolean {
        return other is SuggestionMangaModel && other.manga.id == manga.id
    }
}