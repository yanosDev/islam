package de.yanos.islam.data.api

import de.yanos.islam.data.model.alquran.QuranAudioResponse
import de.yanos.islam.data.model.alquran.QuranTextResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.io.File

interface QuranApi {
    @GET("v1/quran/tr.diyanet")
    fun loadQuranTranslation(): Call<QuranTextResponse>

    @GET("v1/quran/tr.transliteration")
    fun loadQuranTransliteration(): Call<QuranTextResponse>

    @GET("v1/quran/ar.alafasy")
    fun loadQuranAudio(): Call<QuranAudioResponse>

    @Streaming
    @GET
    suspend fun downloadAudio(@Url fileUrl: String): ResponseBody
}

fun ResponseBody.saveFile(destinationFile: File) {
    byteStream().use { inputStream ->
        destinationFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
}