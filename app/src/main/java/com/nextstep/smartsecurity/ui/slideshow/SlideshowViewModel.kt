package com.nextstep.smartsecurity.ui.slideshow

import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SlideshowViewModel : ViewModel() {

    private val database: DatabaseReference = Firebase.database.reference

    fun uploadImageData(
        cameraId: Int,
        imageType: String,
        imagePath: String,
        imageName: String,
        timestamp: Long
    ) {
        val data = mapOf(
            "cameraId" to cameraId,
            "imageType" to imageType,
            "imagePath" to imagePath,
            "imageName" to imageName,
            "timestamp" to timestamp
        )
        database.child("faces").child("camera_3").push().setValue(data)
    }
}
