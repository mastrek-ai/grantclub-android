package com.mastrek.grantclub.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mastrek.grantclub.mobile.data.ApiClient
import com.mastrek.grantclub.mobile.data.Channel
import com.mastrek.grantclub.mobile.data.M3uParser
import com.mastrek.grantclub.mobile.data.MobileSession
import com.mastrek.grantclub.mobile.databinding.FragmentChannelListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChannelListFragment : Fragment() {

    private var _binding: FragmentChannelListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ChannelAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChannelListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChannelAdapter { channel -> openPlayer(channel) }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter       = adapter

        binding.swipeRefresh.setOnRefreshListener { loadChannels() }
        loadChannels()
    }

    private fun loadChannels() {
        binding.swipeRefresh.isRefreshing = true
        val deviceKey = MobileSession.deviceKey ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            val auth = ApiClient.authDevice(deviceKey).getOrNull()
            if (auth != null) {
                MobileSession.status        = auth.status
                MobileSession.daysRemaining = auth.daysRemaining

                val url = auth.playlistUrl
                if (url != null) {
                    MobileSession.playlistUrl = url
                    val m3u      = ApiClient.fetchPlaylist(url).getOrNull() ?: ""
                    val channels = M3uParser.parse(m3u)
                    withContext(Dispatchers.Main) {
                        adapter.submitList(channels)
                        binding.swipeRefresh.isRefreshing = false
                        binding.tvEmpty.visibility = if (channels.isEmpty()) View.VISIBLE else View.GONE
                    }
                    return@launch
                }
            }
            withContext(Dispatchers.Main) {
                binding.swipeRefresh.isRefreshing = false
                binding.tvEmpty.visibility = View.VISIBLE
            }
        }
    }

    private fun openPlayer(channel: Channel) {
        startActivity(Intent(requireContext(), PlayerActivity::class.java).apply {
            putExtra("stream_url",   channel.url)
            putExtra("channel_name", channel.name)
            putExtra("channel_logo", channel.logoUrl)
        })
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
