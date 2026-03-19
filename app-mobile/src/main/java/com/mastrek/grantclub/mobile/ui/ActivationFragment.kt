package com.mastrek.grantclub.mobile.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.mastrek.grantclub.mobile.data.ApiClient
import com.mastrek.grantclub.mobile.data.MobileSession
import com.mastrek.grantclub.mobile.databinding.FragmentActivationMobileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActivationFragment : Fragment() {

    private var _binding: FragmentActivationMobileBinding? = null
    private val binding get() = _binding!!
    private var currentCode: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentActivationMobileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        generateCode()
        binding.btnNewCode.setOnClickListener { generateCode() }
    }

    private fun generateCode() {
        binding.tvCode.text    = "··· - ···"
        binding.tvStatus.text  = "Connecting…"
        lifecycleScope.launch(Dispatchers.IO) {
            val result = ApiClient.registerDevice()
            withContext(Dispatchers.Main) {
                result.onSuccess { data ->
                    val code = data["code"] as? String ?: return@onSuccess
                    currentCode = code
                    binding.tvCode.text   = code
                    binding.tvStatus.text = "Go to grant-club.com/activate"
                    startPolling(code)
                }.onFailure {
                    binding.tvStatus.text = "Network error — retry"
                }
            }
        }
    }

    private fun startPolling(code: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            repeat(120) {
                delay(5000)
                if (code != currentCode) return@launch
                val activated = ApiClient.checkActivation(code).getOrElse { false }
                if (activated) {
                    withContext(Dispatchers.Main) { navigateToBrowse() }
                    return@launch
                }
            }
        }
    }

    private fun navigateToBrowse() {
        parentFragmentManager.beginTransaction()
            .replace(com.mastrek.grantclub.mobile.R.id.mobile_container, ChannelListFragment())
            .commit()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
