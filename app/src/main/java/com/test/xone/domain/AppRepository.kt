package com.test.xone.domain

import kotlinx.coroutines.flow.Flow

interface AppRepository {
    suspend fun addImage(imageEntity: ImageEntity)

    suspend fun deleteImage(imageEntity: ImageEntity)

    fun getImageList(): Flow<List<ImageEntity>>
}