package com.nextstep.smartsecurity.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nextstep.smartsecurity.data.AppDatabase
import com.nextstep.smartsecurity.data.Image
import kotlinx.coroutines.launch

class CameraViewModel(private val appDatabase: AppDatabase) : ViewModel() {

    private val database: DatabaseReference = Firebase.database.reference

    fun uploadImageData(
        cameraId: Int,
        imageType: String,
        imageUrl: String,
        imageName: String,
        timestamp: Long
    ) {
        val data = mapOf(
            "cameraId" to cameraId,
            "imageType" to imageType,
            "imageUrl" to imageUrl,
            "imageName" to imageName,
            "timestamp" to timestamp
        )
        database.child("camera_3").child("camera_3").push().setValue(data)

        // Save image data to local database
        val image = Image(cameraId = cameraId, imageType = imageType, imageUrl = imageUrl, imageName = imageName, timestamp = timestamp)
        viewModelScope.launch {
            appDatabase.imageDao().insert(image)
        }
    }
}