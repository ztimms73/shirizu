package org.xtimms.tokusho.sections.settings.backup

import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import org.xtimms.tokusho.core.base.viewmodel.KotatsuBaseViewModel
import org.xtimms.tokusho.data.repository.backup.BackupRepository
import org.xtimms.tokusho.data.repository.backup.BackupZipOutput
import org.xtimms.tokusho.utils.lang.MutableEventFlow
import org.xtimms.tokusho.utils.lang.call
import java.io.File
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val repository: BackupRepository,
    @ApplicationContext context: Context,
) : KotatsuBaseViewModel() {

    val progress = MutableStateFlow(-1f)
    val onBackupDone = MutableEventFlow<File>()

    init {
        launchLoadingJob {
            val file = BackupZipOutput(context).use { backup ->
                val step = 1f / 6f
                backup.put(repository.createIndex())

                progress.value = 0f
                backup.put(repository.dumpHistory())

                progress.value += step
                backup.put(repository.dumpCategories())

                progress.value += step
                backup.put(repository.dumpFavourites())

                progress.value += step
                backup.put(repository.dumpBookmarks())

                progress.value += step
                backup.put(repository.dumpSources())

                backup.finish()
                progress.value = 1f
                backup.file
            }
            onBackupDone.call(file)
        }
    }
}