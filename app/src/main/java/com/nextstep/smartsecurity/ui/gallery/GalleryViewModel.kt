package com.nextstep.smartsecurity.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextstep.smartsecurity.data.local.AppDatabase
import com.nextstep.smartsecurity.data.local.Image

class GalleryViewModel(private val appDatabase: AppDatabase) : ViewModel() {

    private val _images = MutableLiveData<List<Image>>()
    val images: LiveData<List<Image>> = _images

    init {
        fetchImagesFromFirebase()
    }

    private fun fetchImagesFromFirebase() {

    }

    fun getImagesFromLocal() {

    }
}
