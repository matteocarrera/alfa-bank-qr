package com.example.cloud_cards.DAO

import androidx.room.*
import com.example.cloud_cards.Entities.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE parentId = uuid")
    fun getOwnerUser(): User?

    @Update(entity = User::class)
    fun updateUser(user: User)

    @Insert
    fun insertUser(user: User)

    @Delete
    fun deleteUser(user: User)
}
