package com.test.xone.domain

import androidx.lifecycle.LiveData

interface AppRepository {
    suspend fun addImage(imageEntity: ImageEntity)

    suspend fun deleteImage(imageEntity: ImageEntity)

    fun getImageList(): LiveData<List<ImageEntity>>
}