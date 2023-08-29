package de.yanos.islam.data.api

import de.yanos.islam.data.model.Qibla
import de.yanos.islam.data.model.QiblaData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface QiblaApi {
    @GET("v1/qibla/{latitude}/{longitude}")
    fun getDirection(@Path("latitude") latitude: Double, @Path("longitude") longitude: Double): Call<QiblaData>
}