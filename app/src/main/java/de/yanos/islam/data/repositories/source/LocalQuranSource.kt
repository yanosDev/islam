package de.yanos.islam.data.repositories.source

import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.database.dao.QuranDao
import de.yanos.islam.data.model.quran.Ayah
import de.yanos.islam.data.model.quran.Page
import de.yanos.islam.data.model.quran.Surah
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface LocalQuranSource {
    suspend fun insertSure(sure: Surah, ayahs: List<Ayah>)
    suspend fun saveLocalAudio(id: Int, localAudio: String)
    fun loadPages(): Flow<List<Page>>
    suspend fun isQuranLoaded(): Boolean
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

    override suspend fun saveLocalAudio(id: Int, localAudio: String) {
        withContext(dispatcher) {
            dao.updateLocalAudio(id, localAudio)
        }
    }

    override fun loadPages(): Flow<List<Page>> {
        return combine(flow {
            emit(withContext(dispatcher) {
                dao.sureList()
            })
        }, dao.ayahs()) { surahs, ayahs ->
            ayahs.groupBy { it.page }.map { Page(it.key, surahs.find { surah -> surah.id == it.value.first().sureId }!!.name, it.value) }
        }
    }

    override suspend fun isQuranLoaded(): Boolean {
        return withContext(dispatcher) { dao.ayahSize() == 6236 }
    }
}