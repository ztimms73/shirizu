package org.xtimms.shirizu.sections.search.global.model

import org.koitharu.kotatsu.parsers.model.ContentType
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaParserSource
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.shirizu.core.model.ListModel

sealed interface SearchSuggestionItem : ListModel {

    data class MangaList(
        val items: List<Manga>,
    ) : SearchSuggestionItem {

        override fun areItemsTheSame(other: ListModel): Boolean {
            return other is MangaList
        }
    }

    data class RecentQuery(
        val query: String,
    ) : SearchSuggestionItem {

        override fun areItemsTheSame(other: ListModel): Boolean {
            return other is RecentQuery && query == other.query
        }
    }

    data class Hint(
        val query: String,
    ) : SearchSuggestionItem {

        override fun areItemsTheSame(other: ListModel): Boolean {
            return other is Hint && query == other.query
        }
    }

    data class Author(
        val name: String,
    ) : SearchSuggestionItem {

        override fun areItemsTheSame(other: ListModel): Boolean {
            return other is Author && name == other.name
        }
    }

    data class Source(
        val source: MangaParserSource,
        val isEnabled: Boolean,
    ) : SearchSuggestionItem {

        val isNsfw: Boolean
            get() = source.contentType == ContentType.HENTAI

        override fun areItemsTheSame(other: ListModel): Boolean {
            return other is Source && other.source == source
        }
    }
}