package com.mastrek.grantclub

import android.app.Application
import com.mastrek.grantclub.data.SessionManager

class GrantClubApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
    }
}
