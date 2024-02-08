package org.xtimms.tokusho.sections.details.domain

import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.core.text.getSpans
import androidx.core.text.parseAsHtml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.runInterruptible
import org.koitharu.kotatsu.parsers.exception.NotFoundException
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.util.recoverNotNull
import org.koitharu.kotatsu.parsers.util.runCatchingCancellable
import org.xtimms.tokusho.core.parser.MangaDataRepository
import org.xtimms.tokusho.core.parser.MangaIntent
import org.xtimms.tokusho.core.parser.MangaRepository
import org.xtimms.tokusho.sections.details.data.MangaDetails
import org.xtimms.tokusho.utils.lang.sanitize
import java.io.IOException
import javax.inject.Inject

class DetailsLoadUseCase @Inject constructor(
    private val mangaRepositoryFactory: MangaRepository.Factory,
    private val mangaDataRepository: MangaDataRepository,
    private val imageGetter: Html.ImageGetter,
) {

    operator fun invoke(intent: MangaIntent): Flow<MangaDetails> = channelFlow {
        val manga = requireNotNull(mangaDataRepository.resolveIntent(intent)) {
            "Cannot resolve intent $intent"
        }
        send(MangaDetails(manga, null, false))
        try {
            val details = getDetails(manga)
            send(MangaDetails(details, details.description?.parseAsHtml(withImages = false), false))
            send(MangaDetails(details, details.description?.parseAsHtml(withImages = true), true))
        } catch (e: IOException) {
            throw e
        }
    }

    private suspend fun getDetails(seed: Manga) = runCatchingCancellable {
        val repository = mangaRepositoryFactory.create(seed.source)
        repository.getDetails(seed)
    }.getOrThrow()

    private suspend fun String.parseAsHtml(withImages: Boolean): CharSequence? {
        return if (withImages) {
            runInterruptible(Dispatchers.IO) {
                parseAsHtml(imageGetter = imageGetter)
            }.filterSpans()
        } else {
            runInterruptible(Dispatchers.Default) {
                parseAsHtml()
            }.filterSpans().sanitize()
        }.takeUnless { it.isBlank() }
    }

    private fun Spanned.filterSpans(): Spanned {
        val spannable = SpannableString.valueOf(this)
        val spans = spannable.getSpans<ForegroundColorSpan>()
        for (span in spans) {
            spannable.removeSpan(span)
        }
        return spannable
    }
}