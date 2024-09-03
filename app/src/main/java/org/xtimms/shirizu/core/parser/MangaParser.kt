package org.xtimms.shirizu.core.parser

import org.koitharu.kotatsu.parsers.MangaLoaderContext
import org.koitharu.kotatsu.parsers.MangaParser
import org.koitharu.kotatsu.parsers.model.MangaParserSource

fun MangaParser(source: MangaParserSource, loaderContext: MangaLoaderContext): MangaParser {
    return loaderContext.newParserInstance(source)
}