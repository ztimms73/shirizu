package org.xtimms.shirizu.sections.details.domain

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
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.util.runCatchingCancellable
import org.xtimms.shirizu.core.model.isLocal
import org.xtimms.shirizu.core.parser.MangaDataRepository
import org.xtimms.shirizu.core.parser.MangaIntent
import org.xtimms.shirizu.core.parser.MangaRepository
import org.xtimms.shirizu.core.parser.local.LocalMangaRepository
import org.xtimms.shirizu.sections.details.data.MangaDetails
import org.xtimms.shirizu.utils.lang.peek
import org.xtimms.shirizu.utils.lang.sanitize
import java.io.IOException
import javax.inject.Inject

class DetailsLoadUseCase @Inject constructor(
    private val mangaRepositoryFactory: MangaRepository.Factory,
    private val mangaDataRepository: MangaDataRepository,
    private val localMangaRepository: LocalMangaRepository,
    private val imageGetter: Html.ImageGetter,
) {

    operator fun invoke(mangaId: Long): Flow<MangaDetails> = channelFlow {
        val manga = requireNotNull(mangaDataRepository.findMangaById(mangaId)) {
            "Cannot resolve id $mangaId"
        }
        val local = if (!manga.isLocal) {
            async {
                localMangaRepository.findSavedManga(manga)
            }
        } else {
            null
        }
        send(MangaDetails(manga, null, null, false))
        try {
            val details = getDetails(manga)
            send(MangaDetails(details, local?.peek(), details.description?.parseAsHtml(withImages = false), false))
            send(MangaDetails(details, local?.await(), details.description?.parseAsHtml(withImages = true), true))
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