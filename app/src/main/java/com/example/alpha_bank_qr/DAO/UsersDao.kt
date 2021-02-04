package com.example.alpha_bank_qr.DAO

import androidx.room.*
import com.example.alpha_bank_qr.Entities.User

@Dao
interface UsersDao {
    @Query("SELECT * FROM users WHERE parentId = uuid")
    fun getOwnerUser(): User

    @Query("SELECT COUNT(*) FROM users")
    fun checkUserExists(): Int

    @Update()
    fun updateUser(user: User)

    @Insert
    fun insertUser(user: User)

    @Delete
    fun deleteUser(user: User)
}