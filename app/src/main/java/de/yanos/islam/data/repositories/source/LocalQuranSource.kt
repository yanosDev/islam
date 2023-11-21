package de.yanos.islam.data.repositories.source

import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.database.dao.QuranDao
import de.yanos.islam.data.model.quran.Ayah
import de.yanos.islam.data.model.quran.Surah
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface LocalQuranSource {
    suspend fun insertSure(sure: Surah, ayahs: List<Ayah>)
}

class LocalQuranSourceImpl @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val dao: QuranDao,
) : LocalQuranSource {
    override suspend fun insertSure(sure: Surah, ayahs: List<Ayah>) {
        withContext(dispatcher) {
            dao.insertSure(sure, ayahs)
        }
    }

}