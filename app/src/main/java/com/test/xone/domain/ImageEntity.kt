package com.test.xone.domain

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ImageEntity(
    val imageUri: String,
    var isChecked: Boolean = false,
    val id: Int = UNDEFINED_ID
) {
    companion object {
        const val UNDEFINED_ID = 0
        fun getUniqueName(uniqueNumber: Int? = null): String {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            return "image_${uniqueNumber ?: ""}_$timeStamp.jpg"
        }
    }
}
