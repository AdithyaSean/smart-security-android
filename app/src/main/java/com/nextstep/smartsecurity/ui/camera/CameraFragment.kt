package com.nextstep.smartsecurity.ui.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nextstep.smartsecurity.data.AppDatabase
import com.nextstep.smartsecurity.databinding.FragmentCameraBinding

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CameraViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val appDatabase = AppDatabase.getDatabase(requireContext())
        viewModel = ViewModelProvider(this, CameraViewModelFactory(appDatabase))[CameraViewModel::class.java]

        checkPermissionsAndStartCamera()
        binding.uploadButton.setOnClickListener {
            captureImage()
        }

        return root
    }

    private fun checkPermissionsAndStartCamera() {
        // Implement permission check and start camera logic here
    }

    private fun captureImage() {
        // Implement image capture logic here
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}