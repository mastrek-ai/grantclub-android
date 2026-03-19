package com.mastrek.grantclub.mobile.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mastrek.grantclub.mobile.R
import com.mastrek.grantclub.mobile.data.MobileSession
import com.mastrek.grantclub.mobile.databinding.ActivityMainMobileBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainMobileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMobileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            val fragment = if (MobileSession.isActivated()) {
                ChannelListFragment()
            } else {
                ActivationFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.mobile_container, fragment)
                .commit()
        }
    }
}
