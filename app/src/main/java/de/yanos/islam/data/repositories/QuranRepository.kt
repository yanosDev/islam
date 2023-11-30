@file:UnstableApi

package de.yanos.islam.data.repositories

import androidx.media3.common.util.UnstableApi
import de.yanos.islam.data.model.QuranBookmark
import de.yanos.islam.data.model.quran.Ayah
import de.yanos.islam.data.model.quran.Page
import de.yanos.islam.data.model.quran.Surah
import de.yanos.islam.data.repositories.source.LocalQuranSource
import de.yanos.islam.data.repositories.source.RemoteQuranSource
import de.yanos.islam.util.LoadState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface QuranRepository {
    suspend fun isWholeQuranFetched(): Boolean
    suspend fun fetchQuran()
    fun loadAyahs(): Flow<List<Ayah>>
    fun loadBookmarks(): Flow<List<QuranBookmark>>
    fun subscribeSurahAyahs(id: Int): Flow<List<Ayah>>
    suspend fun loadAyahById(ayahId: Int): Ayah?
    suspend fun loadFirstAyahBySurahId(surahId: Int): Ayah?
    suspend fun loadFirstAyahByPageId(pageId: Int): Ayah?
    suspend fun loadFirstAyahByJuz(juz: Int): Ayah?
    suspend fun loadMedia(id: String, uri: String)
    suspend fun loadAllAyahAudio()
    suspend fun loadAllLearningVideos()
    suspend fun sureList(): List<Surah>
    suspend fun ayahSize(): Int
    suspend fun ayahList(): List<Ayah>
    suspend fun createBookmarkByPage(page: Page, ayah: Ayah?)
}

class QuranRepositoryImpl @Inject constructor(
    private val local: LocalQuranSource,
    private val remote: RemoteQuranSource
) : QuranRepository {
    override suspend fun fetchQuran() {
        val audio = remote.loadQuranAudio()
        val translation = remote.loadQuranTranslation()
        val transliteration = remote.loadQuranTransliteration()
        if (audio is LoadState.Data && translation is LoadState.Data && transliteration is LoadState.Data) {
            audio.data.data.surahs.forEach { surahAudio ->
                val translationSurah = translation.data.data.surahs.find { it.number == surahAudio.number }
                val transliterationSurah = transliteration.data.data.surahs.find { it.number == surahAudio.number }
                val firstJuz = surahAudio.ayahs.first().juz
                val lastJuz = surahAudio.ayahs.last().juz
                val surah = Surah(
                    id = surahAudio.number,
                    name = surahAudio.name,
                    engName = surahAudio.englishName,
                    ayahCount = surahAudio.ayahs.count(),
                    meaning = surahAudio.englishNameTranslation,
                    revelation = surahAudio.revelationType,
                    juz = if (firstJuz == lastJuz) firstJuz.toString() else "$firstJuz - $lastJuz"
                )
                val ayahs = surahAudio.ayahs.mapIndexed { index, ayahAudio ->
                    Ayah(
                        id = ayahAudio.number,
                        sureId = ayahAudio.numberInSurah,
                        sureName = surahAudio.englishName,
                        number = ayahAudio.number,
                        audio = ayahAudio.audio,
                        audioAlt = ayahAudio.audioSecondary.firstOrNull(),
                        text = ayahAudio.text,
                        translationTr = translationSurah?.ayahs?.get(index)?.text ?: "",
                        transliterationEn = transliterationSurah?.ayahs?.get(index)?.text ?: "",
                        juz = ayahAudio.juz,
                        page = ayahAudio.page
                    )
                }
                local.insertSure(surah, ayahs)
            }
        }
    }

    override fun loadAyahs(): Flow<List<Ayah>> {
        return local.loadAyahs()
    }

    override fun loadBookmarks(): Flow<List<QuranBookmark>> {
        return local.loadBookmarks()
    }

    override suspend fun loadFirstAyahBySurahId(surahId: Int): Ayah? {
        return local.loadFirstAyahBySurahId(surahId)
    }

    override suspend fun loadFirstAyahByPageId(pageId: Int): Ayah? {
        return local.loadFirstAyahByPageId(pageId)
    }

    override suspend fun loadFirstAyahByJuz(juz: Int): Ayah? {
        return local.loadFirstAyahByJuz(juz)
    }

    override suspend fun loadMedia(id: String, uri: String) {
        remote.loadMedia(id, uri)
    }

    override suspend fun loadAllAyahAudio() {
        local.ayahList().forEach {
            remote.loadMedia(it.id.toString(), it.audio)
        }
    }

    override suspend fun loadAllLearningVideos() {
        local.learningList().forEach {
            remote.loadMedia(it.id, it.remoteUrl)
        }
    }

    override fun subscribeSurahAyahs(id: Int): Flow<List<Ayah>> {
        return local.loadSurahAyahs(id)
    }

    override suspend fun sureList(): List<Surah> {
        return local.sureList()
    }

    override suspend fun ayahSize(): Int {
        return local.ayahSize()
    }

    override suspend fun ayahList(): List<Ayah> {
        return local.ayahList()
    }

    override suspend fun createBookmarkByPage(page: Page, ayah: Ayah?) {
        val ref = ayah.takeIf { it?.page == page.page } ?: page.ayahs.first()
        local.createBookmark(QuranBookmark(page = ref.page, juz = ref.juz, surahName = ref.sureName, ayah = ref.number, ayahId = ref.id))
    }

    override suspend fun loadAyahById(ayahId: Int): Ayah? {
        return local.loadAyahById(ayahId)
    }

    override suspend fun isWholeQuranFetched(): Boolean {
        return local.isQuranLoaded()
    }
}

