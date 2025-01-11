package com.nextstep.smartsecurity.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ImageDao {
    @Insert
    suspend fun insert(image: ImageEntity)

    @Query("SELECT * FROM images ORDER BY timestamp DESC")
    fun getAllImages(): LiveData<List<ImageEntity>>
}