package com.example.cloud_cards.Utils

import android.widget.ImageView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class ImageUtils {
    companion object {
        private lateinit var mStorageRef: StorageReference

        fun getImageFromFirebase(child: String, imageView: ImageView) {
            try {
                mStorageRef = FirebaseStorage.getInstance().reference
                mStorageRef.child(child).downloadUrl
                    .addOnSuccessListener {
                        val uri = it.toString().substring(0, it.toString().indexOf("&token"))
                        Picasso.get().load(uri).into(imageView)
                    }.addOnFailureListener { }
            } catch (e : Exception) {
                imageView.setImageDrawable(null)
            }
        }
    }
}