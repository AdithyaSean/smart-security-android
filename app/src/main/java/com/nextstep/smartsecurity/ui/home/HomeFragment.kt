package com.nextstep.smartsecurity.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.nextstep.smartsecurity.R
import com.nextstep.smartsecurity.databinding.FragmentHomeBinding
import com.nextstep.smartsecurity.service.CameraDiscoveryService.CameraInfo
import com.nextstep.smartsecurity.service.CameraDiscoveryService.ScanningState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var cameraAdapter: CameraAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        cameraAdapter = CameraAdapter(
            onCameraClick = { camera -> 
                if (camera.isReachable) {
                    // TODO: Open video stream 
                    val streamUrl = viewModel.getStreamUrl(camera)
                    Toast.makeText(requireContext(), "Opening stream: $streamUrl", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), R.string.camera_not_reachable, Toast.LENGTH_SHORT).show()
                }
            }
        )
        
        binding.recyclerView.apply {
            adapter = cameraAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.apply {
            setColorSchemeColors(
                ContextCompat.getColor(requireContext(), R.color.primary)
            )
            setOnRefreshListener {
                viewModel.refresh()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Collect cameras
                launch {
                    viewModel.cameras.collectLatest { cameras ->
                        updateCameraList(cameras)
                    }
                }
                
                // Collect refreshing state
                launch {
                    viewModel.isRefreshing.collectLatest { isRefreshing ->
                        binding.swipeRefresh.isRefreshing = isRefreshing
                    }
                }
                
                // Collect scanning state
                launch {
                    viewModel.scanningState.collectLatest { state ->
                        when (state) {
                            is ScanningState.Idle -> {
                                binding.progressBar.visibility = View.GONE
                                binding.statusText.visibility = View.GONE
                            }
                            is ScanningState.Scanning -> {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.statusText.apply {
                                    text = getString(R.string.scanning_for_cameras)
                                    visibility = View.VISIBLE
                                }
                            }
                            is ScanningState.Error -> {
                                binding.progressBar.visibility = View.GONE
                                binding.statusText.apply {
                                    text = state.message
                                    setTextColor(ContextCompat.getColor(context, R.color.error))
                                    visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
                
                // Collect errors
                launch {
                    viewModel.lastError.collectLatest { error ->
                        error?.let {
                            Snackbar.make(
                                binding.root,
                                error,
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun updateCameraList(cameras: List<CameraInfo>) {
        if (cameras.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyView.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            cameraAdapter.submitList(cameras)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
