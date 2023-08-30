package de.yanos.islam.data.repositories.source

import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.database.dao.AwqatDao
import de.yanos.islam.data.model.awqat.AwqatDailyContent
import de.yanos.islam.util.LoadState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface LocalAwqatSource {
    suspend fun insertDailyContent(dailyContentData: AwqatDailyContent)
}

class LocalAwqatSourceImpl @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val dao: AwqatDao
) : LocalAwqatSource {
    override suspend fun insertDailyContent(dailyContentData: AwqatDailyContent) {
        withContext(dispatcher) {
            dao.insertDailyContent(dailyContentData)
        }
    }
}