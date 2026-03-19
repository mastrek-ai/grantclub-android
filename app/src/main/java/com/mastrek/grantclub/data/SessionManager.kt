package com.mastrek.grantclub.data

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREFS_NAME  = "gc_session"
    private const val KEY_DEVICE  = "device_key"
    private const val KEY_USER    = "user_id"
    private const val KEY_STATUS  = "status"
    private const val KEY_DAYS    = "days_remaining"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
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

    fun isActivated() = deviceKey != null
    fun clear()       = prefs.edit().clear().apply()
}
