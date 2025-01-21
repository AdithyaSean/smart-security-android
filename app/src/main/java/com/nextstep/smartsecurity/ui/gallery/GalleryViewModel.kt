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
        val databaseReference = Firebase.database.reference.child("faces").child("camera_3")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val firebaseImages = mutableListOf<Image>()
                for (data in snapshot.children) {
                    val image = data.getValue(Image::class.java)
                    image?.let {
                        Log.d("GalleryViewModel", "Fetched image: ${it.imageUrl}")
                        firebaseImages.add(it)
                    }
                }
                _images.postValue(firebaseImages)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("GalleryViewModel", "Error fetching images from Firebase: ${error.message}")
            }
        })
    }

    private fun getImagesFromLocal() {
        viewModelScope.launch {
            appDatabase.imageDao().getAllImages().observeForever { localImages ->
                _images.postValue(localImages)
            }
        }
    }
}
