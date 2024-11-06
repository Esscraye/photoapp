package ca.baleras.uqacalbumphoto

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log

fun getDevicePhotos(context: Context): List<String> {
    val photos = mutableListOf<String>()
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val cursor = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        null
    )
    cursor?.use {
        val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (it.moveToNext()) {
            val id = it.getLong(columnIndex)
            val photoUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
            Log.d("getDevicePhotos TAG", "Photo found: $photoUri")
            photos.add(photoUri.toString())
        }
    } ?: Log.d("getDevicePhotos TAG", "Cursor is null")
    return photos
}