package de.yanos.islam.data.api

import de.yanos.islam.data.model.tanzil.TanzilQuranResponse
import de.yanos.islam.data.model.tanzil.TanzilSureResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface TanzilApi {
    @GET("/kuran")
    fun loadQuran(): Call<TanzilQuranResponse>

    @GET("/kuran/sure/{sureNr}")
    fun loadSure(@Path("sureNr") nr: String): Call<TanzilSureResponse>
}