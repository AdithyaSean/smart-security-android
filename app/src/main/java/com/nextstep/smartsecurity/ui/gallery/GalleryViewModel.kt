package com.nextstep.smartsecurity.ui.gallery

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nextstep.smartsecurity.data.AppDatabase
import com.nextstep.smartsecurity.data.Image
import kotlinx.coroutines.launch

class GalleryViewModel(private val appDatabase: AppDatabase) : ViewModel() {

    private val _images = MutableLiveData<List<Image>>()
    val images: LiveData<List<Image>> = _images

    init {
        fetchImagesFromFirebase()
        getImagesFromLocal()
    }

    private fun fetchImagesFromFirebase() {
        val databaseReference = Firebase.database.reference.child("camera_3")
        val cameras = listOf("camera_1", "camera_2", "camera_3")
        val firebaseImages = mutableListOf<Image>()

        cameras.forEach { camera ->
            databaseReference.child(camera).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        val image = data.getValue(Image::class.java)
                        image?.let {
                            Log.d("GalleryViewModel", "Fetched image from $camera: ${it.imageUrl}")
                            firebaseImages.add(it)
                        }
                    }
                    _images.postValue(firebaseImages)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("GalleryViewModel", "Error fetching images from $camera: ${error.message}")
                }
            })
        }
    }

    private fun getImagesFromLocal() {
        viewModelScope.launch {
            appDatabase.imageDao().getAllImages().observeForever { localImages ->
                val currentImages = _images.value.orEmpty().toMutableList()
                currentImages.addAll(localImages)
                _images.postValue(currentImages)
            }
        }
    }
}