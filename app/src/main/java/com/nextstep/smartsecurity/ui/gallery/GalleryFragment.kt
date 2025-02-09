package com.nextstep.smartsecurity.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.nextstep.smartsecurity.R
import com.nextstep.smartsecurity.data.AppDatabase
import com.nextstep.smartsecurity.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var imageAdapter: ImageAdapter
    private var unknownCount = 0
    private var showingUnknownOnly = false

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

        setupMenu()

        imageAdapter = ImageAdapter { image, isKnown ->
            galleryViewModel.markImageAsKnown(image.id, isKnown)
        }
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = imageAdapter
            Log.d("GalleryFragment", "RecyclerView initialized")
        }

        observeViewModel()

        return root
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_gallery, menu)
                // Update the unknown faces count and checked state
                val unknownItem = menu.findItem(R.id.action_unknown)
                unknownItem.isChecked = showingUnknownOnly
                if (unknownCount > 0) {
                    unknownItem.title = getString(R.string.unknown_faces_count, unknownCount)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_unknown -> {
                        showingUnknownOnly = !showingUnknownOnly
                        menuItem.isChecked = showingUnknownOnly
                        updateImageList()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun observeViewModel() {
        galleryViewModel.images.observe(viewLifecycleOwner) { images ->
            Log.d("GalleryFragment", "Images observed: ${images.size}")
            updateImageList()
        }

        galleryViewModel.unknownFacesCount.observe(viewLifecycleOwner) { count ->
            unknownCount = count
            requireActivity().invalidateOptionsMenu()
        }

        galleryViewModel.unknownFaces.observe(viewLifecycleOwner) { unknownFaces ->
            if (showingUnknownOnly) {
                imageAdapter.submitList(unknownFaces)
            }
        }
    }

    private fun updateImageList() {
        if (showingUnknownOnly) {
            galleryViewModel.unknownFaces.value?.let { unknownFaces ->
                imageAdapter.submitList(unknownFaces)
            }
        } else {
            galleryViewModel.images.value?.let { allImages ->
                imageAdapter.submitList(allImages)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
