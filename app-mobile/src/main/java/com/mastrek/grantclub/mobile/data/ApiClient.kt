package com.mastrek.grantclub.mobile.data

import com.mastrek.grantclub.mobile.BuildConfig
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

object ApiClient {
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val gson = Gson()
    private val base = BuildConfig.API_BASE_URL

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private fun post(path: String, body: Map<String, Any>): Result<Map<*, *>> = runCatching {
        val req = Request.Builder()
            .url("$base$path")
            .post(gson.toJson(body).toRequestBody(JSON))
            .build()
        client.newCall(req).execute().use { res ->
            val text = res.body?.string() ?: ""
            val map  = gson.fromJson(text, Map::class.java)
            if (!res.isSuccessful) error(map["error"]?.toString() ?: "ERROR")
            map
        }
    }

    fun registerDevice(): Result<Map<*, *>> = post("/api/devices/register", emptyMap())

    fun checkActivation(code: String): Result<Boolean> = runCatching {
        post("/api/devices/check", mapOf("code" to code)).getOrThrow()["activated"] as? Boolean ?: false
    }

    fun authDevice(deviceKey: String): Result<DeviceAuthResponse> = runCatching {
        val m = post("/api/devices/auth", mapOf("deviceKey" to deviceKey)).getOrThrow()
        DeviceAuthResponse(
            status        = m["status"]?.toString()        ?: "inactive",
            daysRemaining = (m["daysRemaining"] as? Double)?.toInt() ?: 0,
            userId        = (m["userId"] as? Double)?.toInt()        ?: 0,
            deviceKey     = m["deviceKey"]?.toString()     ?: deviceKey,
            playlistUrl   = m["playlistUrl"]?.toString()
        )
    }

    fun fetchPlaylist(url: String): Result<String> = runCatching {
        val req = Request.Builder().url(url).get().build()
        client.newCall(req).execute().use { it.body?.string() ?: error("EMPTY") }
    }
}

data class DeviceAuthResponse(
    val status: String, val daysRemaining: Int, val userId: Int,
    val deviceKey: String, val playlistUrl: String?
)
