package org.xtimms.tokusho.core.exceptions

import okhttp3.Headers
import okio.IOException
import org.koitharu.kotatsu.parsers.model.MangaSource

class CloudflareProtectedException(
    val url: String,
    val source: MangaSource?,
    @Transient val headers: Headers,
) : IOException("Protected by Cloudflare")