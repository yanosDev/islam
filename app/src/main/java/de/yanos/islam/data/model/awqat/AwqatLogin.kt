package de.yanos.islam.data.model.awqat

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AwqatLoginResponse(val data: AwqatLogin)

@JsonClass(generateAdapter = true)
data class AwqatLogin(val accessToken: String, val refreshToken: String)

@JsonClass(generateAdapter = true)
data class Login(val email: String, val password: String)