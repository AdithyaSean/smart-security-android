package com.nextstep.smartsecurity.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.nextstep.smartsecurity.data.AppDatabase
import com.nextstep.smartsecurity.databinding.FragmentGalleryBinding
import com.nextstep.smartsecurity.ui.camera.ImageAdapter

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val appDatabase = AppDatabase.getDatabase(requireContext())
        galleryViewModel = ViewModelProvider(this, GalleryViewModelFactory(appDatabase)).get(GalleryViewModel::class.java)
        Log.d("GalleryFragment", "ViewModel initialized")

        imageAdapter = ImageAdapter()
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = imageAdapter
            Log.d("GalleryFragment", "RecyclerView initialized")
        }

        galleryViewModel.images.observe(viewLifecycleOwner) { images ->
            Log.d("GalleryFragment", "Images observed: ${images.size}")
            imageAdapter.submitList(images)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}