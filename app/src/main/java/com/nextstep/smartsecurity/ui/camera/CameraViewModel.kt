package com.nextstep.smartsecurity.ui.camera

import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CameraViewModel : ViewModel() {

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
        database.child("images").child("camera_3").push().setValue(data)
    }
}
