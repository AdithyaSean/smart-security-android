package com.nextstep.smartsecurity.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nextstep.smartsecurity.data.local.AppDatabase
import com.nextstep.smartsecurity.data.local.Image
import kotlinx.coroutines.launch
import android.util.Log

class GalleryViewModel(private val appDatabase: AppDatabase) : ViewModel() {

    private val _images = MutableLiveData<List<Image>>()
    val images: LiveData<List<Image>> = _images

    init {
        fetchImagesFromFirebase()
    }

    private fun fetchImagesFromFirebase() {
        val database = Firebase.database.reference.child("faces")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val imageList = mutableListOf<Image>()
                for (child in snapshot.children) {
                    val cameraId = child.key ?: continue
                    Log.d("GalleryViewModel", "Processing images for camera: $cameraId")
                    for (imageSnapshot in child.children) {
                        Log.d("GalleryViewModel", "ImageSnapshot: ${imageSnapshot.value}")
                        val image = imageSnapshot.getValue(Image::class.java)
                        if (image != null) {
                            imageList.add(image)
                            viewModelScope.launch {
                                val existingImage = appDatabase.imageDao().getImageById(image.cameraId)
                                if (existingImage == null) {
                                    appDatabase.imageDao().insert(image)
                                }
                            }
                        }
                    }
                }
                _images.value = imageList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun getImagesFromLocal() {
        viewModelScope.launch {
            appDatabase.imageDao().getAllImages().observeForever { imageList ->
                _images.value = imageList
            }
        }
    }
}
