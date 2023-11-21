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
            audio.data.data.surahs.forEach { surah ->
                val translationSurah = translation.data.data.surahs.find { it.number == surah.number }
                val transliterationSurah = transliteration.data.data.surahs.find { it.number == surah.number }
                val firstJuz = surah.ayahs.first().juz
                val lastJuz = surah.ayahs.last().juz
                val sure = Surah(
                    id = surah.number,
                    name = surah.name,
                    engName = surah.englishName,
                    ayahCount = surah.ayahs.count(),
                    meaning = surah.englishNameTranslation,
                    revelation = surah.revelationType,
                    juz = if (firstJuz == lastJuz) firstJuz.toString() else "$firstJuz - $lastJuz"
                )
                val ayahs = surah.ayahs.mapIndexed { index, ayahAudio ->
                    Ayah(
                        id = ayahAudio.number,
                        sureId = surah.number,
                        sureName = surah.englishName,
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
                local.insertSure(sure, ayahs)
            }
        }
    }
}