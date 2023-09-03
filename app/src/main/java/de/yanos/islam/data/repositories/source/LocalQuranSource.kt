package de.yanos.islam.data.repositories.source

import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.database.dao.QuranDao
import de.yanos.islam.data.model.quran.Ayet
import de.yanos.islam.data.model.tanzil.SureDetail
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface LocalQuranSource {
    suspend fun saveQuranSummary(kuran: List<SureDetail>)
    suspend fun saveSure(map: List<Ayet>)
}

class LocalQuranSourceImpl @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val dao: QuranDao
) : LocalQuranSource {
    override suspend fun saveQuranSummary(kuran: List<SureDetail>) {
        withContext(dispatcher) {
            dao.insert(kuran)
        }
    }

    override suspend fun saveSure(ayet: List<Ayet>) {
        withContext(dispatcher) {
            dao.insertSure(ayet)
        }
    }
}