package com.mastrek.grantclub.mobile.ui

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.mastrek.grantclub.mobile.databinding.ActivityPlayerMobileBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerMobileBinding
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = ActivityPlayerMobileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url  = intent.getStringExtra("stream_url")   ?: return finish()
        val name = intent.getStringExtra("channel_name") ?: ""

        binding.tvChannelName.text = name
        binding.btnPip.setOnClickListener { enterPip() }
        binding.btnClose.setOnClickListener { finish() }

        initPlayer(url)
    }

    private fun initPlayer(url: String) {
        player = ExoPlayer.Builder(this).build().also { exo ->
            binding.playerView.player = exo
            exo.setMediaItem(MediaItem.fromUri(url))
            exo.playWhenReady = true
            exo.prepare()
            exo.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    binding.progressBar.visibility = if (state == Player.STATE_BUFFERING)
                        android.view.View.VISIBLE else android.view.View.GONE
                }
            })
        }
    }

    private fun enterPip() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val params = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(16, 9))
                .build()
            enterPictureInPictureMode(params)
        }
    }

    override fun onPictureInPictureModeChanged(isInPip: Boolean, config: Configuration) {
        super.onPictureInPictureModeChanged(isInPip, config)
        binding.btnPip.visibility   = if (isInPip) android.view.View.GONE else android.view.View.VISIBLE
        binding.btnClose.visibility = if (isInPip) android.view.View.GONE else android.view.View.VISIBLE
        binding.tvChannelName.visibility = if (isInPip) android.view.View.GONE else android.view.View.VISIBLE
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        enterPip()
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
