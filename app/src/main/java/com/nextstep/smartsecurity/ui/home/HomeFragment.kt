package com.nextstep.smartsecurity.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
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

        val webView1: WebView = binding.webView1
        val webView2: WebView = binding.webView2

        webView1.settings.javaScriptEnabled = true
        webView2.settings.javaScriptEnabled = true

        webView1.webViewClient = WebViewClient()
        webView2.webViewClient = WebViewClient()

        webView1.loadUrl("http://192.168.1.15:2003/video_feed/1")
        webView2.loadUrl("http://192.168.1.16:2003/video_feed/2")
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
