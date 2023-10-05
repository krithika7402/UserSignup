package com.example.cruddemo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// UserDao.kt
@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user_data WHERE email = :email AND password = :password")
    suspend fun getUserByEmailAndPassword(email: String, password: String): User?
}
