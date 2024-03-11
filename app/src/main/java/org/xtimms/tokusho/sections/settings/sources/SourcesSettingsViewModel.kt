package org.xtimms.tokusho.sections.settings.sources

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.xtimms.tokusho.core.base.viewmodel.KotatsuBaseViewModel
import org.xtimms.tokusho.data.repository.MangaSourcesRepository
import javax.inject.Inject

@HiltViewModel
class SourcesSettingsViewModel @Inject constructor(
    sourcesRepository: MangaSourcesRepository,
) : KotatsuBaseViewModel() {

    private val mutableViewStateFlow = MutableStateFlow(ViewState())
    val viewStateFlow = mutableViewStateFlow.asStateFlow()

    data class ViewState(
        val enabledSourcesCount: Int = -1,
        val availableSourcesCount: Int = -1,
    )

    val enabledSourcesCount = sourcesRepository.observeEnabledSourcesCount()

    val availableSourcesCount = sourcesRepository.observeAvailableSourcesCount()
}
