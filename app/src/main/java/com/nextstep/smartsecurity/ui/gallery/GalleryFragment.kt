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
import com.nextstep.smartsecurity.R
import com.nextstep.smartsecurity.data.local.AppDatabase
import com.nextstep.smartsecurity.data.local.Image
import com.nextstep.smartsecurity.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private lateinit var galleryViewModel: GalleryViewModel

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
