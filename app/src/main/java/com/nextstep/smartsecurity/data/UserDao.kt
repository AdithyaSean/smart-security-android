package com.nextstep.smartsecurity.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUserById(uid: String): User?
}