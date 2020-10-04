package com.example.alpha_bank_qr.DAO

import androidx.room.*
import com.example.alpha_bank_qr.Entities.User

@Dao //add userBooleanDAO (findById, insert, delete)
interface UserDao {
    @Query("SELECT * FROM users WHERE parentId = uuid")
    fun getOwnerUser() : User

    @Query("SELECT * FROM users")
    fun getAllUsers() : List<User>

    @Query("SELECT * FROM users WHERE uuid = :uuid")
    fun getUserById(uuid : String) : User

    @Update(entity = User::class)
    fun updateUser(user: User)

    @Insert
    fun insertUser(user : User)

    @Delete
    fun deleteUser(user : User)
}
