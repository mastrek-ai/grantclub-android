package com.mastrek.grantclub.ui

import android.content.Intent
import android.os.Bundle
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.mastrek.grantclub.data.ApiClient
import com.mastrek.grantclub.data.Channel
import com.mastrek.grantclub.data.SessionManager
import com.mastrek.grantclub.service.M3uParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BrowseFragment : BrowseSupportFragment() {

    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title          = "Grant Club IPTV"
        headersState   = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        adapter        = rowsAdapter
        loadChannels()
    }

    private fun loadChannels() {
        val deviceKey = SessionManager.deviceKey ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            // Auth device → récupérer URL playlist
            val authResult = ApiClient.authDevice(deviceKey)
            authResult.onSuccess { auth ->
                SessionManager.status       = auth.status
                SessionManager.daysRemaining = auth.daysRemaining

                val playlistUrl = auth.playlistUrl
                if (playlistUrl != null) {
                    val m3uResult = ApiClient.fetchPlaylist(playlistUrl)
                    m3uResult.onSuccess { content ->
                        val channels = M3uParser.parse(content)
                        withContext(Dispatchers.Main) {
                            buildRows(channels)
                        }
                    }
                }
            }
        }
    }

    private fun buildRows(channels: List<Channel>) {
        rowsAdapter.clear()
        val groups = channels.groupBy { it.group ?: "Other" }

        groups.forEach { (groupName, groupChannels) ->
            val presenter  = CardPresenter()
            val listAdapter = ArrayObjectAdapter(presenter)
            groupChannels.forEach { listAdapter.add(it) }

            val header = HeaderItem(groupName)
            rowsAdapter.add(ListRow(header, listAdapter))
        }

        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            if (item is Channel) openPlayer(item)
        }
    }

    private fun openPlayer(channel: Channel) {
        val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
            putExtra("stream_url",    channel.url)
            putExtra("channel_name",  channel.name)
            putExtra("channel_logo",  channel.logoUrl)
        }
        startActivity(intent)
    }
}
