package com.nextstep.smartsecurity.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nextstep.smartsecurity.R

class GalleryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var galleryAdapter: GalleryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        recyclerView = root.findViewById(R.id.recycler_view)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val photos = listOf(R.drawable.photo1, R.drawable.photo1, R.drawable.photo1, R.drawable.photo1, R.drawable.photo1, R.drawable.photo1, R.drawable.photo1, R.drawable.photo1, R.drawable.photo1, R.drawable.photo1) // Add your photo resources here
        galleryAdapter = GalleryAdapter(requireContext(), photos)
        recyclerView.layoutManager = GridLayoutManager(context, 2) // Set the number of columns
        recyclerView.adapter = galleryAdapter
    }
}