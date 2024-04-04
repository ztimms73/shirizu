package org.xtimms.etsudoku.sections.reader

import org.xtimms.etsudoku.sections.reader.pager.ReaderPage

data class ReaderContent(
    val pages: List<ReaderPage>,
    val state: ReaderState?
)