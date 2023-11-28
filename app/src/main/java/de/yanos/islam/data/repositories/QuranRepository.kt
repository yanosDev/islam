@file:UnstableApi

package de.yanos.islam.data.repositories

import android.content.Context
import androidx.media3.common.util.UnstableApi
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun loadPages(): Flow<List<Page>>
    fun loadAyahs(): Flow<List<Ayah>>
    fun subscribeSurahAyahs(id: Int): Flow<List<Ayah>>
    suspend fun loadAyahById(ayahId: Int): Ayah?
    suspend fun loadFirstAyahBySurahId(surahId: Int): Ayah?
    suspend fun loadFirstAyahByPageId(pageId: Int): Ayah?
    suspend fun loadFirstAyahByJuz(juz: Int): Ayah?
    suspend fun loadAyahAudio(id: Int, uri: String)
    suspend fun loadAllAyahAudio()
    suspend fun sureList(): List<Surah>
}

class QuranRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
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
                        sureId = surahAudio.number,
                        sureName = surahAudio.englishName,
                        number = ayahAudio.number,
                        audio = ayahAudio.audio,
                        audioMore = ayahAudio.audioSecondary.firstOrNull(),
                        localAudio = null,
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

    override fun loadPages(): Flow<List<Page>> {
        return local.loadPages()
    }

    override fun loadAyahs(): Flow<List<Ayah>> {
        return local.loadAyahs()
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

    override suspend fun loadAyahAudio(id: Int, uri: String) {
        remote.loadAyahAudio(id, uri)
    }

    override suspend fun loadAllAyahAudio() {
        local.ayahList().forEach {
            remote.loadAyahAudio(it.id, it.audio)
        }
    }

    override fun subscribeSurahAyahs(id: Int): Flow<List<Ayah>> {
        return local.subscribeSurahAyahs(id)
    }

    override suspend fun sureList(): List<Surah> {
        return local.sureList()
    }

    override suspend fun loadAyahById(ayahId: Int): Ayah? {
        return local.loadAyahById(ayahId)
    }

    override suspend fun isWholeQuranFetched(): Boolean {
        return local.isQuranLoaded()
    }
}

