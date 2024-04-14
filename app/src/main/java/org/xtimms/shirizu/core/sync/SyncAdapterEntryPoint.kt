package org.xtimms.shirizu.core.sync

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SyncAdapterEntryPoint {
    val syncHelperFactory: SyncHelper.Factory
}