package com.nextstep.smartsecurity.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.fragment.app.Fragment
import com.nextstep.smartsecurity.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize WebView
        val webView: WebView = binding.webView
        webView.webViewClient = WebViewClient()
        webView.loadUrl("http://192.168.1.18:2003/")

        // Initialize Buttons for Audio Communication
        val btnCamera1Audio: Button = binding.btnCamera1Audio
        val btnCamera2Audio: Button = binding.btnCamera2Audio

        btnCamera1Audio.setOnClickListener {
            // Handle Camera 1 Audio Communication
            startAudioCommunication("camera1")
        }

        btnCamera2Audio.setOnClickListener {
            // Handle Camera 2 Audio Communication
            startAudioCommunication("camera2")
        }

        return root
    }

    private fun startAudioCommunication(cameraId: String) {
        // Implement audio communication logic here
        // For example, using WebRTC or another audio streaming library
        when (cameraId) {
            "camera1" -> {
                // Start audio stream for Camera 1
            }
            "camera2" -> {
                // Start audio stream for Camera 2
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
