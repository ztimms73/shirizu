package org.xtimms.tokusho.sections.reader

import org.xtimms.tokusho.sections.reader.pager.ReaderPage

data class ReaderContent(
    val pages: List<ReaderPage>,
    val state: ReaderState?
)