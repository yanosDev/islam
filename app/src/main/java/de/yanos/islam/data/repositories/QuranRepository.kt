package de.yanos.islam.data.repositories

import de.yanos.islam.data.model.quran.Ayah
import de.yanos.islam.data.model.quran.Surah
import de.yanos.islam.data.repositories.source.LocalQuranSource
import de.yanos.islam.data.repositories.source.RemoteQuranSource
import de.yanos.islam.util.LoadState
import javax.inject.Inject

interface QuranRepository {
    suspend fun fetchQuran()
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
                        sureId = surahAudio.number,
                        sureName = surahAudio.englishName,
                        number = ayahAudio.number,
                        audio = ayahAudio.audio,
                        audioMore = ayahAudio.audioSecondary.firstOrNull(),
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
}