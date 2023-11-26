@file:UnstableApi

package de.yanos.islam.data.repositories

import android.content.Context
import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.islam.data.model.quran.Ayah
import de.yanos.islam.data.model.quran.Page
import de.yanos.islam.data.model.quran.Surah
import de.yanos.islam.data.repositories.source.LocalQuranSource
import de.yanos.islam.data.repositories.source.RemoteQuranSource
import de.yanos.islam.service.ExoDownloadService
import de.yanos.islam.util.LoadState
import de.yanos.islam.util.localFile
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

interface QuranRepository {
    suspend fun isWholeQuranFetched(): Boolean
    suspend fun fetchQuran()
    suspend fun loadAudio(ayah: Ayah): File?
    fun loadPages(): Flow<List<Page>>
    fun loadAyahs(): Flow<List<Ayah>>
    suspend fun loadAyahById(ayahId: Int): Ayah?
    suspend fun loadFirstAyahBySurahId(surahId: Int): Ayah?
    suspend fun loadFirstAyahByPageId(pageId: Int): Ayah?
    suspend fun loadFirstAyahByJuz(juz: Int): Ayah?
    suspend fun loadAudioAlt(ayah: Ayah)
}

class QuranRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val filesDir: File,
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

    override suspend fun loadAudio(ayah: Ayah): File? {
        val audioFile = ayah.audio.localFile(filesDir)
        val audioAlt = ayah.audioMore?.localFile(filesDir)
        val finalFile = if (audioFile.exists()) {
            audioFile
        } else {
            val loadState = remote.downloadAudio(ayah.audio)
            if (loadState is LoadState.Data)
                loadState.data
            else if (audioAlt != null) {
                val altLoadState = remote.downloadAudio(ayah.audioMore)
                if (altLoadState is LoadState.Data)
                    audioAlt
                else null
            } else null
        }
        local.saveLocalAudio(ayah.id, audioFile.absolutePath)
        return finalFile
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

    override suspend fun loadAudioAlt(ayah: Ayah) {
        val downloadRequest: DownloadRequest = DownloadRequest.Builder(ayah.id.toString(), Uri.parse(ayah.audio)).build()
        DownloadService.sendAddDownload(
            context,
            ExoDownloadService::class.java,
            downloadRequest,
            false
        )
    }

    override suspend fun loadAyahById(ayahId: Int): Ayah? {
        return local.loadAyahById(ayahId)
    }

    override suspend fun isWholeQuranFetched(): Boolean {
        return local.isQuranLoaded()
    }
}

