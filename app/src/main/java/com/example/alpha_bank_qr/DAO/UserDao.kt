package com.example.alpha_bank_qr.DAO

import androidx.room.*
import com.example.alpha_bank_qr.Entities.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE isOwner = 1")
    fun getOwnerUser() : User

    @Query("SELECT * FROM users WHERE isOwner = 0 AND isScanned = 0")
    fun getUsersFromMyCards() : List<User>

    @Query("SELECT * FROM users WHERE isScanned = 1")
    fun getScannedUsers() : List<User>

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id : Int) : User

    @Query("SELECT id FROM users WHERE id = (SELECT MAX(id) FROM users)")
    fun getLastUserId() : Int

    @Update(entity = User::class)
    fun updateUser(user: User)

    @Insert
    fun insertUser(user : User)

    @Delete
    fun deleteUser(user : User)
}
