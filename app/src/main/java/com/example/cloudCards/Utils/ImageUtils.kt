package com.example.cloudCards.Utils

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

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
            } catch (e: Exception) {
                imageView.setImageDrawable(null)
            }
        }

        fun getImageInByteArray(drawable: Drawable?): ByteArray? {
            if (drawable != null) {
                val bitmap = (drawable as BitmapDrawable).bitmap
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                return stream.toByteArray()
            }
            return null
        }

        fun getImageInByteArray(bitmap: Bitmap): ByteArray {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()
        }

        fun getImageInDrawable(cursor: Cursor, column: String): Drawable? {
            val blob = cursor.getBlob(cursor.getColumnIndex(column))
            if (blob != null)
                return BitmapDrawable(BitmapFactory.decodeByteArray(blob, 0, blob.size))
            return null
        }
    }
}