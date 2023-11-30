package de.yanos.islam.data.repositories.source

import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.database.dao.BookmarkDao
import de.yanos.islam.data.database.dao.QuranDao
import de.yanos.islam.data.database.dao.VideoDao
import de.yanos.islam.data.model.QuranBookmark
import de.yanos.islam.data.model.VideoLearning
import de.yanos.islam.data.model.quran.Ayah
import de.yanos.islam.data.model.quran.Surah
import de.yanos.islam.util.Constants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface LocalQuranSource {
    suspend fun isQuranLoaded(): Boolean
    suspend fun insertSure(sure: Surah, ayahs: List<Ayah>)

    suspend fun loadAyahById(ayahId: Int): Ayah?
    suspend fun loadFirstAyahBySurahId(surahId: Int): Ayah?
    suspend fun loadFirstAyahByPageId(pageId: Int): Ayah?
    suspend fun loadFirstAyahByJuz(juz: Int): Ayah?

    fun loadAyahs(): Flow<List<Ayah>>
    fun loadBookmarks(): Flow<List<QuranBookmark>>
    fun loadSurahAyahs(id: Int): Flow<List<Ayah>>

    suspend fun sureList(): List<Surah>
    suspend fun ayahList(): List<Ayah>
    suspend fun ayahSize(): Int
    suspend fun learningList(): List<VideoLearning>
    suspend fun createBookmark(bookmark: QuranBookmark)
}

class LocalQuranSourceImpl @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val dao: QuranDao,
    private val bookmarkDao: BookmarkDao,
    private val videoDao: VideoDao
) : LocalQuranSource {
    override suspend fun insertSure(sure: Surah, ayahs: List<Ayah>) {
        withContext(dispatcher) {
            dao.insertSure(sure, ayahs)
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

    override fun loadAyahs(): Flow<List<Ayah>> {
        return dao.subscribeAyahs()
    }

    override fun loadBookmarks(): Flow<List<QuranBookmark>> {
        return bookmarkDao.loadBookmarks()
    }

    override fun loadSurahAyahs(id: Int): Flow<List<Ayah>> {
        return dao.subsribeSurahAyahs(id)
    }

    override suspend fun sureList(): List<Surah> {
        return withContext(dispatcher) {
            dao.sureList()
        }
    }

    override suspend fun ayahList(): List<Ayah> {
        return withContext(dispatcher) { dao.ayahList() }
    }

    override suspend fun ayahSize(): Int {
        return withContext(dispatcher) { dao.ayahSize() }
    }

    override suspend fun learningList(): List<VideoLearning> {
        return withContext(dispatcher) { videoDao.loadAll() }
    }

    override suspend fun createBookmark(bookmark: QuranBookmark) {
        withContext(dispatcher) {
            bookmarkDao.insert(bookmark)
        }
    }

    override suspend fun isQuranLoaded(): Boolean {
        return withContext(dispatcher) { dao.ayahSize() == Constants.AYAH_TOTAL }
    }

}