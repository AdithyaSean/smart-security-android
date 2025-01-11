package com.nextstep.smartsecurity.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nextstep.smartsecurity.R
import com.nextstep.smartsecurity.data.local.AppDatabase
import com.nextstep.smartsecurity.data.local.ImageEntity
import com.nextstep.smartsecurity.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private lateinit var galleryViewModel: GalleryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val appDatabase = AppDatabase.getDatabase(requireContext())
        galleryViewModel = ViewModelProvider(this, GalleryViewModelFactory(appDatabase)).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        val adapter = ImageAdapter()
        recyclerView.adapter = adapter

        galleryViewModel.images.observe(viewLifecycleOwner) { images ->
            adapter.submitList(images)
        }

        galleryViewModel.getImagesFromLocal()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

        private var images: List<ImageEntity> = emptyList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
            return ImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            val image = images[position]
            Glide.with(holder.itemView.context)
                .load(image.imageData)
                .into(holder.imageView)
        }

        override fun getItemCount(): Int = images.size

        fun submitList(newImages: List<ImageEntity>) {
            images = newImages
            notifyDataSetChanged()
        }

        private inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.imageView)
        }
    }
}