package com.mastrek.grantclub.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.mastrek.grantclub.R
import com.mastrek.grantclub.data.ApiClient
import com.mastrek.grantclub.data.SessionManager
import com.mastrek.grantclub.databinding.FragmentActivationBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActivationFragment : Fragment() {

    private var _binding: FragmentActivationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActivationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Générer et afficher le code d'activation
        generateAndDisplayCode()

        binding.btnRefresh.setOnClickListener {
            generateAndDisplayCode()
        }
    }

    private fun generateAndDisplayCode() {
        binding.tvStatus.text = getString(R.string.activation_waiting)
        binding.tvStatus.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            // Enregistrer le device pour obtenir un code
            val result = ApiClient.registerDevice()
            withContext(Dispatchers.Main) {
                result.onSuccess { data ->
                    val code = data["code"] as? String ?: ""
                    binding.tvCode.text = code
                    binding.tvStatus.text = getString(R.string.activation_go_to)
                    // Polling toutes les 5 secondes
                    startPolling(code)
                }.onFailure {
                    binding.tvStatus.text = getString(R.string.activation_error)
                }
            }
        }
    }

    private fun startPolling(code: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            repeat(120) { // 10 minutes max
                kotlinx.coroutines.delay(5000)
                val result = ApiClient.checkActivation(code)
                result.onSuccess { activated ->
                    if (activated) {
                        withContext(Dispatchers.Main) { onActivated() }
                        return@launch
                    }
                }
            }
        }
    }

    private fun onActivated() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_container, BrowseFragment())
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
