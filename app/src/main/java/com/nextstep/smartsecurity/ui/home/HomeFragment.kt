package com.nextstep.smartsecurity.ui.home

import android.os.Bundle
import android.view.*
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
        binding.webView1.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
        }
        binding.webView2.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
        }

        // Set WebViewClient to handle page loading
        binding.webView1.webViewClient = WebViewClient()
        binding.webView2.webViewClient = WebViewClient()

        // Setup audio buttons
        binding.audioButton1.setOnClickListener {
            // Add logic to handle audio for the first stream
        }

        binding.audioButton2.setOnClickListener {
            // Add logic to handle audio for the second stream
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
            binding.webView1.loadUrl("http://${camera1.host}:${camera1.port}${camera1.streamPath}")
        }

        if (cameras.size > 1) {
            val camera2 = cameras[1]
            binding.webView2.loadUrl("http://${camera2.host}:${camera2.port}${camera2.streamPath}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
