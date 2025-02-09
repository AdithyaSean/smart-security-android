package com.nextstep.smartsecurity.ui.home

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.nextstep.smartsecurity.R
import com.nextstep.smartsecurity.databinding.FragmentHomeBinding
import com.nextstep.smartsecurity.service.CameraDiscoveryService.CameraInfo

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    
    private var audioEnabled1 = false
    private var audioEnabled2 = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        setupMenu()
        setupWebViews()
        observeViewModel()

        return root
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_home, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_refresh -> {
                        viewModel.startDiscovery()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupWebViews() {
        // Configure WebView settings
        val configureWebView = { webView: WebView ->
            webView.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                mediaPlaybackRequiresUserGesture = false
                useWideViewPort = true
                loadWithOverviewMode = true
                // Enable hardware acceleration for better performance
                webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
                // Cache settings for better performance
                cacheMode = WebSettings.LOAD_NO_CACHE
                // Enable mixed content (HTTP content in HTTPS page)
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
        }
        
        configureWebView(binding.webView1)
        configureWebView(binding.webView2)

        // Set WebViewClient to handle page loading
        val webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Log.e("WebView", "Error: code=$errorCode, desc=$description, url=$failingUrl")
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d("WebView", "Started loading: $url")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("WebView", "Finished loading: $url")
            }
        }
        binding.webView1.webViewClient = webViewClient
        binding.webView2.webViewClient = webViewClient

        // Setup audio buttons
        binding.audioButton1.setOnClickListener {
            audioEnabled1 = !audioEnabled1
            binding.audioButton1.setImageResource(
                if (audioEnabled1) R.drawable.ic_volume_up else R.drawable.ic_volume_off
            )
            binding.webView1.settings.mediaPlaybackRequiresUserGesture = !audioEnabled1
            // Reload stream to apply audio setting
            viewModel.cameras.value?.firstOrNull()?.let { camera ->
                binding.webView1.loadUrl("http://${camera.host}:${camera.port}${camera.streamPath}")
            }
        }

        binding.audioButton2.setOnClickListener {
            audioEnabled2 = !audioEnabled2
            binding.audioButton2.setImageResource(
                if (audioEnabled2) R.drawable.ic_volume_up else R.drawable.ic_volume_off
            )
            binding.webView2.settings.mediaPlaybackRequiresUserGesture = !audioEnabled2
            // Reload stream to apply audio setting
            viewModel.cameras.value?.getOrNull(1)?.let { camera ->
                binding.webView2.loadUrl("http://${camera.host}:${camera.port}${camera.streamPath}")
            }
        }
    }

    private fun observeViewModel() {
        viewModel.cameras.observe(viewLifecycleOwner) { cameras ->
            updateCameraViews(cameras)
        }

        viewModel.isScanning.observe(viewLifecycleOwner) { isScanning ->
            binding.progressBar.isVisible = isScanning
            if (isScanning) {
                binding.noCamerasText.isVisible = false
            }
        }
    }

    private fun updateCameraViews(cameras: List<CameraInfo>) {
        binding.noCamerasText.isVisible = cameras.isEmpty()
        binding.linearLayout1.isVisible = cameras.isNotEmpty()
        binding.linearLayout2.isVisible = cameras.size > 1

            if (cameras.isNotEmpty()) {
                val camera1 = cameras[0]
                val url1 = "http://${camera1.host}:${camera1.port}${camera1.streamPath}"
                Log.d("Camera", "Loading camera 1: $url1")
                binding.webView1.loadUrl(url1)
            }

            if (cameras.size > 1) {
                val camera2 = cameras[1]
                val url2 = "http://${camera2.host}:${camera2.port}${camera2.streamPath}"
                Log.d("Camera", "Loading camera 2: $url2")
                binding.webView2.loadUrl(url2)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
