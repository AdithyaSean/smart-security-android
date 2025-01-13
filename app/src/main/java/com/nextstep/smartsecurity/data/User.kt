package com.nextstep.smartsecurity.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
class User(
    @PrimaryKey val uid: String,
    val name: String,
    val email: String
)