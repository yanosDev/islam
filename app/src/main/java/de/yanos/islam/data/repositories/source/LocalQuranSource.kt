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
    suspend fun isQuranLoaded(): Boolean
    suspend fun insertSure(sure: Surah, ayahs: List<Ayah>)
    suspend fun saveLocalAudio(id: Int, localAudio: String)

    suspend fun loadAyahById(ayahId: Int): Ayah?
    suspend fun loadFirstAyahBySurahId(surahId: Int): Ayah?
    suspend fun loadFirstAyahByPageId(pageId: Int): Ayah?
    suspend fun loadFirstAyahByJuz(juz: Int): Ayah?

    fun loadPages(): Flow<List<Page>>
    fun loadAyahs(): Flow<List<Ayah>>
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

    override suspend fun loadAyahById(ayahId: Int): Ayah? {
        return withContext(dispatcher) {
            dao.loadAyah(ayahId)
        }
    }

    override suspend fun loadFirstAyahBySurahId(surahId: Int): Ayah? {
        return withContext(dispatcher) {
            dao.loadFirstAyahBySurahId(surahId)
        }
    }

    override suspend fun loadFirstAyahByPageId(pageId: Int): Ayah? {
        return withContext(dispatcher) {
            dao.loadFirstAyahByPageId(pageId)
        }
    }

    override suspend fun loadFirstAyahByJuz(juz: Int): Ayah? {
        return withContext(dispatcher) {
            dao.loadFirstAyahByJuz(juz)
        }
    }

    override fun loadPages(): Flow<List<Page>> {
        return combine(flow {
            emit(withContext(dispatcher) {
                dao.sureList()
            })
        }, dao.subscribeAyahs()) { surahs, ayahs ->
            ayahs.groupBy { it.page }.map {
                surahs.find { surah -> surah.id == it.value.first().sureId }?.let { surah ->
                    Page(it.key, surah.name, surah.id, it.value)
                }
            }.filterNotNull()
        }
    }

    override fun loadAyahs(): Flow<List<Ayah>> {
        return dao.subscribeAyahs()
    }

    override suspend fun isQuranLoaded(): Boolean {
        return withContext(dispatcher) { dao.ayahSize() == 6236 }
    }
}