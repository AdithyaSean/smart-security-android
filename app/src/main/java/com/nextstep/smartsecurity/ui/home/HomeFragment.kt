package com.nextstep.smartsecurity.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nextstep.smartsecurity.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        val webView1: WebView = binding.webView1
        val webView2: WebView = binding.webView2

        webView1.loadUrl("http://192.168.1.18:2003/video_feed/1")
        webView2.loadUrl("http://192.168.1.18:2003/video_feed/2")

        binding.audioButton1.setOnClickListener {
            // Add logic to handle audio for the first stream
        }

        binding.audioButton2.setOnClickListener {
            // Add logic to handle audio for the second stream
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
