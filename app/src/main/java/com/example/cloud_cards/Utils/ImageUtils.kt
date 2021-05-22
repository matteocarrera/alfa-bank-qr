package com.example.cloud_cards.Utils

import android.widget.ImageView
import com.squareup.picasso.Picasso

class ImageUtils {
    companion object {
        fun getImageFromFirebase(uuid: String, imageView: ImageView) {
            val uri = "https://firebasestorage.googleapis.com/v0/b/cloudcardsmobile.appspot.com/o/${uuid}?alt=media"
            Picasso.get()
                .load(uri)
                .into(imageView)
        }
    }
}