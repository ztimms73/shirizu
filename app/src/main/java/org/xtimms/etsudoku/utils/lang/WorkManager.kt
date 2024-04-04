package org.xtimms.etsudoku.utils.lang

import android.annotation.SuppressLint
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.await

@SuppressLint("RestrictedApi")
suspend fun WorkManager.awaitUniqueWorkInfoByName(name: String): List<WorkInfo> {
    return getWorkInfosForUniqueWork(name).await().orEmpty()
}