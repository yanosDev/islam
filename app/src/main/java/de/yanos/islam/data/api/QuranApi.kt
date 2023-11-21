package de.yanos.islam.data.api

import de.yanos.islam.data.model.alquran.QuranAudioResponse
import de.yanos.islam.data.model.alquran.QuranTextResponse
import retrofit2.Call
import retrofit2.http.GET

interface QuranApi {
    @GET("v1/quran/tr.diyanet")
    fun loadQuranTranslation(): Call<QuranTextResponse>

    @GET("v1/quran/tr.transliteration")
    fun loadQuranTransliteration(): Call<QuranTextResponse>

    @GET("v1/quran/ar.alafasy")
    fun loadQuranAudio(): Call<QuranAudioResponse>
}
