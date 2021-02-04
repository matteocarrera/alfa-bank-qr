package com.example.cloudCards.DAO

import androidx.room.*
import com.example.cloudCards.Entities.User

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