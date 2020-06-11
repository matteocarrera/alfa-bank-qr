package com.example.alpha_bank_qr.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scanned_uuid")
class ScannedUUID (
    @PrimaryKey val uuid : String
)