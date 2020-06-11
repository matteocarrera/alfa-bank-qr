package com.example.alpha_bank_qr.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.alpha_bank_qr.Entities.ScannedUUID

@Dao
interface ScannedUuidDao {
    @Query("SELECT * FROM scanned_uuid")
    fun getAllUUID() : List<ScannedUUID>

    @Insert
    fun insertScannedUUID(scannedUUID: ScannedUUID)
}