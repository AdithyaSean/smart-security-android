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
import com.nextstep.smartsecurity.data.local.ImageEntity
import kotlinx.coroutines.launch

class GalleryViewModel(private val appDatabase: AppDatabase) : ViewModel() {

    private val _images = MutableLiveData<List<ImageEntity>>()
    val images: LiveData<List<ImageEntity>> = _images

    init {
        fetchImagesFromFirebase()
    }

    private fun fetchImagesFromFirebase() {
        val database = Firebase.database.reference.child("faces")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val imageList = mutableListOf<ImageEntity>()
                for (child in snapshot.children) {
                    val cameraId = child.key ?: continue
                    for (imageSnapshot in child.children) {
                        val image = imageSnapshot.getValue(ImageEntity::class.java)
                        if (image != null) {
                            imageList.add(image)
                            viewModelScope.launch {
                                appDatabase.imageDao().insert(image)
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
            _images.value = appDatabase.imageDao().getAllImages().value
        }
    }
}