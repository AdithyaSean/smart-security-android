package com.nextstep.smartsecurity.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class Image(
    @PrimaryKey val id: Int = 0,
    val cameraId: Int,
    val imageUrl: String,
    val imageName: String,
    val imageType: String,
    val timestamp: Long,
    var isKnown: Boolean = false // Default to false for new faces
) {
    // No-argument constructor required by Firebase
    constructor() : this(0, 0, "", "", "", 0L, false)
}
