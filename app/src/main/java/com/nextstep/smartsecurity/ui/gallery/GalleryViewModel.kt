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

    private val _unknownFacesCount = MutableLiveData<Int>()
    val unknownFacesCount: LiveData<Int> = _unknownFacesCount

    val unknownFaces: LiveData<List<Image>> = appDatabase.imageDao().getImagesByKnownStatus(false)
    val knownFaces: LiveData<List<Image>> = appDatabase.imageDao().getImagesByKnownStatus(true)

    init {
        fetchImagesFromFirebase()
        getImagesFromLocal()
        observeUnknownFacesCount()
    }

    private fun observeUnknownFacesCount() {
        appDatabase.imageDao().getUnknownFacesCount().observeForever { count ->
            _unknownFacesCount.value = count
        }
    }

    fun markImageAsKnown(imageId: Int, isKnown: Boolean) {
        viewModelScope.launch {
            try {
                // Update local database
                appDatabase.imageDao().updateImageKnownStatus(imageId, isKnown)
                Log.d("GalleryViewModel", "Image $imageId marked as ${if (isKnown) "known" else "unknown"} in local DB")

                // Get image details from local DB
                val image = appDatabase.imageDao().getImageById(imageId)
                image?.let {
                    // Update Firebase
                    val cameraRef = Firebase.database.reference
                        .child("images")
                        .child("camera_${it.cameraId}")
                        .child(it.imageName.substringBeforeLast("."))

                    cameraRef.child("isKnown").setValue(isKnown)
                        .addOnSuccessListener {
                            Log.d("GalleryViewModel", "Image status updated in Firebase")
                        }
                        .addOnFailureListener { e ->
                            Log.e("GalleryViewModel", "Error updating Firebase: ${e.message}")
                        }

                    // If marking as known, also update the face recognition database
                    if (isKnown) {
                        Firebase.database.reference
                            .child("known_faces")
                            .child(it.imageName)
                            .setValue(mapOf(
                                "imageUrl" to it.imageUrl,
                                "timestamp" to it.timestamp,
                                "addedAt" to System.currentTimeMillis()
                            ))
                    }
                }
            } catch (e: Exception) {
                Log.e("GalleryViewModel", "Error updating image status: ${e.message}")
            }
        }
    }

    private fun fetchImagesFromFirebase() {
        val databaseReference = Firebase.database.reference.child("images")
        val cameras = listOf("camera_1", "camera_2", "camera_3")
        
        cameras.forEach { camera ->
            databaseReference.child(camera).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val firebaseImages = mutableListOf<Image>()
                    for (data in snapshot.children) {
                        try {
                            val imageMap = data.value as? Map<*, *>
                            if (imageMap != null) {
                                val image = Image(
                                    id = (imageMap["id"] as? Long)?.toInt() ?: 0,
                                    cameraId = (imageMap["cameraId"] as? Long)?.toInt() ?: 0,
                                    imageUrl = imageMap["imageUrl"] as? String ?: "",
                                    imageName = imageMap["imageName"] as? String ?: "",
                                    imageType = imageMap["imageType"] as? String ?: "",
                                    timestamp = imageMap["timestamp"] as? Long ?: 0L,
                                    isKnown = imageMap["isKnown"] as? Boolean ?: false
                                )
                                firebaseImages.add(image)
                                
                                // Update local database
                                viewModelScope.launch {
                                    appDatabase.imageDao().insert(image)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("GalleryViewModel", "Error parsing image data: ${e.message}")
                        }
                    }
                    
                    // Update the LiveData with the latest images
                    val currentImages = _images.value.orEmpty().toMutableList()
                    currentImages.addAll(firebaseImages)
                    _images.postValue(currentImages.distinctBy { it.id })
                    
                    Log.d("GalleryViewModel", "Fetched ${firebaseImages.size} images from $camera")
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
                _images.postValue(currentImages.distinctBy { it.id }) // Ensure no duplicates
            }
        }
    }
}
