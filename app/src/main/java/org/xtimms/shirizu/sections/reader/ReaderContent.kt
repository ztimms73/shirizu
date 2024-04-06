package org.xtimms.shirizu.sections.reader

import org.xtimms.shirizu.sections.reader.pager.ReaderPage

data class ReaderContent(
    val pages: List<ReaderPage>,
    val state: ReaderState?
)