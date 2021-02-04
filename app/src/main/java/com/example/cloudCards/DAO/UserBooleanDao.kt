package com.example.cloudCards.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.cloudCards.Entities.UserBoolean

@Dao
interface UserBooleanDao {
    @Insert
    fun insertUserBoolean(userBoolean: UserBoolean)

    @Delete
    fun deleteUserBoolean(userBoolean: UserBoolean)

    @Query("SELECT * FROM cards")
    fun getAllUsersBoolean(): List<UserBoolean>

    @Query("SELECT * FROM cards WHERE parentId = :id")
    fun getTemplateUsers(id: String): List<UserBoolean>

    @Query("SELECT * FROM cards WHERE parentId != :id")
    fun getContactUsers(id: String): List<UserBoolean>

    @Query("SELECT * FROM cards WHERE uuid = :uuid")
    fun getUserBooleanById(uuid: String): UserBoolean
}