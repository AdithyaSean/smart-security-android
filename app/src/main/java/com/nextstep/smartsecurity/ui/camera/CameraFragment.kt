package com.nextstep.smartsecurity.ui.camera

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.storage.FirebaseStorage
import com.nextstep.smartsecurity.databinding.FragmentCameraBinding
import java.io.File
import java.io.OutputStream

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CameraViewModel
    private var imageCapture: ImageCapture? = null
    private var imageCount = 0

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
        startCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View {
            viewModel = ViewModelProvider(this)[CameraViewModel::class.java]
            _binding = FragmentCameraBinding.inflate(inflater, container, false)
            val root: View = binding.root

            checkPermissionsAndStartCamera()
            binding.uploadButton.setOnClickListener {
                captureImage()
            }

            return root
        }

    private fun checkPermissionsAndStartCamera() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.surfaceProvider = binding.previewView.surfaceProvider
            }
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.e("CameraFragment", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun captureImage() {
        imageCount++
        val timestamp = System.currentTimeMillis()
        val photoFile = File(requireContext().filesDir, "camera_3_time_${timestamp}_frame_${imageCount}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture?.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(requireContext()),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            val imagePath = photoFile.absolutePath
                            Log.d("CameraFragment", "Image saved at: $imagePath")
                            Toast.makeText(requireContext(), "Image saved at: $imagePath", Toast.LENGTH_SHORT).show()
                            uploadImageToFirebaseStorage(photoFile, timestamp)
                            saveImageToMediaStore(photoFile)
                        }

                        override fun onError(exc: ImageCaptureException) {
                            Log.e("captureImage", "Image capture error")
                        }
                    }
                )
    }

    private fun uploadImageToFirebaseStorage(photoFile: File, timestamp: Long) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/${photoFile.name}")
        val uploadTask = imageRef.putFile(photoFile.toUri())

        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                viewModel.uploadImageData(3, "image", imageUrl, photoFile.name, timestamp)
            }
        }.addOnFailureListener {
            Log.e("uploadImageToFirebaseStorage", "Unsuccessful uploads")
        }
    }

    private fun saveImageToMediaStore(photoFile: File) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, photoFile.name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SmartSecurity")
        }

        val resolver = requireContext().contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream: OutputStream ->
                photoFile.inputStream().copyTo(outputStream)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
