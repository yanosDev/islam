package de.yanos.islam.data.api

import de.yanos.islam.data.model.awqat.AwqatCityDetailsResponse
import de.yanos.islam.data.model.awqat.AwqatDailyContentResponse
import de.yanos.islam.data.model.awqat.AwqatEidResponse
import de.yanos.islam.data.model.awqat.AwqatLocationResponse
import de.yanos.islam.data.model.awqat.AwqatLoginResponse
import de.yanos.islam.data.model.awqat.AwqatPrayerTimeResponse
import de.yanos.islam.data.model.awqat.Login
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface AwqatApi {
    @POST("/Auth/Login")
    fun login(@Body login: Login): Call<AwqatLoginResponse>

    @GET("/api/Auth/RefreshToken/{RefreshToken}")
    fun refreshToken(@Path("RefreshToken") refreshToken: String): Call<AwqatLoginResponse>

    @GET("/api/DailyContent")
    fun dailyContent(@Header("Authorization") authToken: String): Call<AwqatDailyContentResponse>

    @GET("/api/Place/Countries")
    fun loadCountries(@Header("Authorization") authToken: String): Call<AwqatLocationResponse>

    @GET("/api/Place/States")
    fun loadStates(@Header("Authorization") authToken: String): Call<AwqatLocationResponse>

    @GET("/api/Place/States/{countryId}")
    fun loadCountryStates(@Header("Authorization") authToken: String, @Path("countryId") countryId: String): Call<AwqatLocationResponse>

    @GET("/api/Place/Cities")
    fun loadCities(@Header("Authorization") authToken: String): Call<AwqatLocationResponse>

    @GET("/api/Place/Cities/{stateId}")
    fun loadStateCities(@Header("Authorization") authToken: String, @Path("stateId") stateId: String): Call<AwqatLocationResponse>

    @GET("/api/Place/CityDetail/{cityId}")
    fun loadCityDetails(@Header("Authorization") authToken: String, @Path("cityId") cityId: Int): Call<AwqatCityDetailsResponse>

    @GET("/api/PrayerTime/Monthly/{cityId}")
    fun loadCityPrayerTimes(@Header("Authorization") authToken: String, @Path("cityId") cityId: Int): Call<AwqatPrayerTimeResponse>
    @GET("/api/PrayerTime/Eid/{cityId}")
    fun loadCityPrayerTimesEid(@Header("Authorization") authToken: String, @Path("cityId") cityId: Int): Call<AwqatEidResponse>
}