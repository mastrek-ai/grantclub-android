package com.mastrek.grantclub.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.mastrek.grantclub.R
import com.mastrek.grantclub.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val streamUrl   = intent.getStringExtra("stream_url")   ?: return
        val channelName = intent.getStringExtra("channel_name")  ?: ""

        binding.tvChannelName.text = channelName
        initPlayer(streamUrl)
    }

    private fun initPlayer(url: String) {
        player = ExoPlayer.Builder(this).build().also { exo ->
            binding.playerView.player = exo
            exo.setMediaItem(MediaItem.fromUri(url))
            exo.playWhenReady = true
            exo.prepare()

            exo.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_BUFFERING) {
                        binding.progressBar.visibility = android.view.View.VISIBLE
                    } else {
                        binding.progressBar.visibility = android.view.View.GONE
                    }
                }
            })
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_BACK,
            KeyEvent.KEYCODE_ESCAPE -> { finish(); true }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
