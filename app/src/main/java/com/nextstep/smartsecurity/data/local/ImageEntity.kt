package com.nextstep.smartsecurity.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey val id: String,
    val cameraId: String,
    val imageType: String,
    val imageData: String,
    val timestamp: Long
)