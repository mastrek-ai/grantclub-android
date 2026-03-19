package com.mastrek.grantclub.data

import com.mastrek.grantclub.BuildConfig
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

object ApiClient {
    private val JSON   = "application/json; charset=utf-8".toMediaType()
    private val gson   = Gson()
    private val base   = BuildConfig.API_BASE_URL

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private fun post(path: String, bodyMap: Map<String, Any>): Result<Map<*, *>> = runCatching {
        val body = gson.toJson(bodyMap).toRequestBody(JSON)
        val req  = Request.Builder().url("$base$path").post(body).build()
        client.newCall(req).execute().use { res ->
            val text = res.body?.string() ?: ""
            val map  = gson.fromJson(text, Map::class.java)
            if (!res.isSuccessful) error(map["error"]?.toString() ?: "ERROR")
            map
        }
    }

    // Enregistrer le device → obtenir code XXXX-XXXX
    fun registerDevice(): Result<Map<*, *>> =
        post("/api/devices/register", emptyMap())

    // Vérifier si le code a été activé depuis le portail
    fun checkActivation(code: String): Result<Boolean> = runCatching {
        val result = post("/api/devices/check", mapOf("code" to code))
        result.getOrThrow()["activated"] as? Boolean ?: false
    }

    // Auth device par deviceKey → statut + playlist URL
    fun authDevice(deviceKey: String): Result<DeviceAuthResponse> = runCatching {
        val map = post("/api/devices/auth", mapOf("deviceKey" to deviceKey)).getOrThrow()
        DeviceAuthResponse(
            status        = map["status"]?.toString()        ?: "inactive",
            daysRemaining = (map["daysRemaining"] as? Double)?.toInt() ?: 0,
            userId        = (map["userId"] as? Double)?.toInt()        ?: 0,
            deviceKey     = map["deviceKey"]?.toString()     ?: deviceKey,
            playlistUrl   = map["playlistUrl"]?.toString()
        )
    }

    // Fetch M3U content
    fun fetchPlaylist(url: String): Result<String> = runCatching {
        val req = Request.Builder().url(url).get().build()
        client.newCall(req).execute().use { res ->
            res.body?.string() ?: error("EMPTY_PLAYLIST")
        }
    }
}
