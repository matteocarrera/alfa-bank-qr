package com.example.alpha_bank_qr.Database

import com.google.firebase.firestore.FirebaseFirestore

abstract class FirestoreInstance {

    companion object {
        private var INSTANCE: FirebaseFirestore? = null

        fun getInstance(): FirebaseFirestore {
            if (INSTANCE == null) {
                INSTANCE = FirebaseFirestore.getInstance()
            }
            return INSTANCE as FirebaseFirestore
        }
    }
}