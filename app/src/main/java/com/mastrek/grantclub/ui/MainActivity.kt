package com.mastrek.grantclub.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.mastrek.grantclub.R
import com.mastrek.grantclub.data.SessionManager

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragment = if (SessionManager.isActivated()) {
                BrowseFragment()
            } else {
                ActivationFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit()
        }
    }
}
