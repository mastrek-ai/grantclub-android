package com.mastrek.grantclub.mobile.data

import android.content.Context
import android.content.SharedPreferences

object MobileSession {
    private const val PREFS       = "gc_mobile_session"
    private const val KEY_DEVICE  = "device_key"
    private const val KEY_USER    = "user_id"
    private const val KEY_STATUS  = "status"
    private const val KEY_DAYS    = "days_remaining"
    private const val KEY_M3U     = "playlist_url"

    private lateinit var prefs: SharedPreferences

    fun init(ctx: Context) {
        prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    var deviceKey: String?
        get()      = prefs.getString(KEY_DEVICE, null)
        set(value) = prefs.edit().putString(KEY_DEVICE, value).apply()

    var userId: Int
        get()      = prefs.getInt(KEY_USER, 0)
        set(value) = prefs.edit().putInt(KEY_USER, value).apply()

    var status: String
        get()      = prefs.getString(KEY_STATUS, "inactive") ?: "inactive"
        set(value) = prefs.edit().putString(KEY_STATUS, value).apply()

    var daysRemaining: Int
        get()      = prefs.getInt(KEY_DAYS, 0)
        set(value) = prefs.edit().putInt(KEY_DAYS, value).apply()

    var playlistUrl: String?
        get()      = prefs.getString(KEY_M3U, null)
        set(value) = prefs.edit().putString(KEY_M3U, value).apply()

    fun isActivated() = deviceKey != null
    fun clear()       = prefs.edit().clear().apply()
}
