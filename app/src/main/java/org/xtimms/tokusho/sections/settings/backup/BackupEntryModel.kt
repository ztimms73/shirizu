package org.xtimms.tokusho.sections.settings.backup

import androidx.annotation.StringRes
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.model.ListModel
import org.xtimms.tokusho.data.repository.backup.BackupEntry

data class BackupEntryModel(
    val name: BackupEntry.Name,
    val isChecked: Boolean,
    val isEnabled: Boolean,
) : ListModel {

    @get:StringRes
    val titleResId: Int
        get() = when (name) {
            BackupEntry.Name.INDEX -> 0 // should not appear here
            BackupEntry.Name.HISTORY -> R.string.history
            BackupEntry.Name.CATEGORIES -> R.string.categories
            BackupEntry.Name.FAVOURITES -> R.string.nav_shelf
            BackupEntry.Name.BOOKMARKS -> R.string.bookmarks
            BackupEntry.Name.SOURCES -> R.string.remote_sources
        }

    override fun areItemsTheSame(other: ListModel): Boolean {
        return other is BackupEntryModel && other.name == name
    }
}