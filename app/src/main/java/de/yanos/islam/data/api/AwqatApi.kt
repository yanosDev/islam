package de.yanos.islam.data.api

import de.yanos.islam.data.model.awqat.AwqatCityDetails
import de.yanos.islam.data.model.awqat.AwqatCityDetailsResponse
import de.yanos.islam.data.model.awqat.AwqatDailyContent
import de.yanos.islam.data.model.awqat.AwqatDailyContentResponse
import de.yanos.islam.data.model.awqat.AwqatLocation
import de.yanos.islam.data.model.awqat.AwqatLocationResponse
import de.yanos.islam.data.model.awqat.AwqatLogin
import de.yanos.islam.data.model.awqat.AwqatLoginResponse
import de.yanos.islam.data.model.awqat.AwqatPrayerTimeResponse
import de.yanos.islam.data.model.awqat.Login
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AwqatApi {
    @POST("/Auth/Login")
    fun login(@Body login: Login): Call<AwqatLoginResponse>

    @GET("/api/Auth/RefreshToken/{RefreshToken}")
    fun refreshToken(@Path("RefreshToken") refreshToken: String): Call<AwqatLoginResponse>

    @GET("/api/DailyContent")
    fun dailyContent(@Header("Authorization") authToken: String): Call<AwqatDailyContentResponse>

    @GET("/api/Place/Countries")
    fun loadCountries(): Call<AwqatLocationResponse>

    @GET("/api/Place/States/{countryId}")
    fun loadCountryStates(): Call<AwqatLocationResponse>

    @GET("/api/Place/Cities/{stateId}")
    fun loadStateCities(): Call<AwqatLocationResponse>

    @GET("/api/Place/CityDetail/{cityId}")
    fun loadCityDetails(): Call<AwqatCityDetailsResponse>

    @GET("/api/PrayerTime/Daily/{cityId}")
    fun loadCityPrayerTimes(): Call<AwqatPrayerTimeResponse>
}