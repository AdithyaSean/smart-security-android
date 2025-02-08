package com.nextstep.smartsecurity.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image: Image)

    @Query("SELECT * FROM images ORDER BY timestamp DESC")
    fun getAllImages(): LiveData<List<Image>>

    @Query("SELECT * FROM images WHERE cameraId = :id LIMIT 1")
    suspend fun getImageById(id: Int): Image?

    @Query("SELECT * FROM images WHERE isKnown = :isKnown ORDER BY timestamp DESC")
    fun getImagesByKnownStatus(isKnown: Boolean): LiveData<List<Image>>

    @Query("UPDATE images SET isKnown = :isKnown WHERE id = :imageId")
    suspend fun updateImageKnownStatus(imageId: Int, isKnown: Boolean)

    @Query("SELECT COUNT(*) FROM images WHERE isKnown = 0")
    fun getUnknownFacesCount(): LiveData<Int>
}
