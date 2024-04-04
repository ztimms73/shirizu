package org.xtimms.etsudoku.core.parser

import org.koitharu.kotatsu.parsers.MangaLoaderContext
import org.koitharu.kotatsu.parsers.MangaParser
import org.koitharu.kotatsu.parsers.model.MangaSource

fun MangaParser(source: MangaSource, loaderContext: MangaLoaderContext): MangaParser {
    return loaderContext.newParserInstance(source)
}