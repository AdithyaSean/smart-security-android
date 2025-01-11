package com.nextstep.smartsecurity.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "images")
data class Image(
    @PrimaryKey val id: Int,
    val cameraId: Int,
    val imageType: String,
    val imageData: String,
    val timestamp: Long,
    val imageName: String
) {
    // No-argument constructor required by Firebase
    constructor() : this(0, 0, "", "", 0L, "")
}
