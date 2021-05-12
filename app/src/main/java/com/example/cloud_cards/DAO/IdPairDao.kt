package com.example.cloud_cards.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.cloud_cards.Entities.Card
import com.example.cloud_cards.Entities.IdPair
import com.example.cloud_cards.Entities.User

@Dao
interface IdPairDao {

    @Query("SELECT * FROM idPairs")
    fun getAllPairs(): List<IdPair>

    @Insert
    fun insertPair(idPair: IdPair)
}