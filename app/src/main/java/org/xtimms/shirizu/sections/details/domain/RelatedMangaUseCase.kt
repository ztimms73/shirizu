package org.xtimms.shirizu.sections.details.domain

import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.util.runCatchingCancellable
import org.xtimms.shirizu.core.parser.MangaRepository
import javax.inject.Inject

class RelatedMangaUseCase @Inject constructor(
    private val mangaRepositoryFactory: MangaRepository.Factory,
) {

    suspend operator fun invoke(seed: Manga) = runCatchingCancellable {
        mangaRepositoryFactory.create(seed.source).getRelated(seed)
    }.onFailure {
        it.printStackTrace()
    }.getOrNull()
}