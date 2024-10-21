package ca.baleras.uqacalbumphoto

import android.content.Context
import android.provider.MediaStore

fun getDevicePhotos(context: Context): List<String> {
    val photos = mutableListOf<String>()
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        null
    )
    cursor?.use {
        val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        while (it.moveToNext()) {
            photos.add(it.getString(columnIndex))
        }
    }
    return photos
}