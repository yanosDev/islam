package de.yanos.islam.data.model.awqat

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AwqatLoginResponse(val data: AwqatLogin)

data class AwqatLogin(val accessToken: String, val refreshToken: String)