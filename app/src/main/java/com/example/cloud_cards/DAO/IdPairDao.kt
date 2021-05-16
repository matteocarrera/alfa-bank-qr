package com.example.cloud_cards.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.cloud_cards.Entities.Card
import com.example.cloud_cards.Entities.IdPair
import com.example.cloud_cards.Entities.User

@Dao
interface IdPairDao {

    @Query("SELECT * FROM idPairs")
    fun getAllPairs(): List<IdPair>

    @Query("SELECT * FROM idPairs WHERE parentUuid <> uuid")
    fun getAllContactsPairs(): List<IdPair>

    @Query("SELECT * FROM idPairs WHERE uuid = :uuid")
    fun getIdPairById(uuid: String): IdPair

    @Insert
    fun insertPair(idPair: IdPair)

    @Delete
    fun deletePair(idPair: IdPair)
}