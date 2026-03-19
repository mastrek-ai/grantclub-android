package com.mastrek.grantclub.mobile

import android.app.Application
import com.mastrek.grantclub.mobile.data.MobileSession

class MobileApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileSession.init(this)
    }
}
