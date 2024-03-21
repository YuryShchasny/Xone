package com.test.xone.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageEntityDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imageUri: String
)