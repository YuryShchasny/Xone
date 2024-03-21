package com.test.xone.data

import com.test.xone.domain.ImageEntity
import javax.inject.Inject

class ImageEntityMapper @Inject constructor() {
    fun mapEntityToDbModel(imageEntity: ImageEntity): ImageEntityDbModel = ImageEntityDbModel(
        id = imageEntity.id,
        imageUri = imageEntity.imageUri
    )

    fun mapDbModelToEntity(imageEntityDbModel: ImageEntityDbModel): ImageEntity = ImageEntity(
        id = imageEntityDbModel.id,
        imageUri = imageEntityDbModel.imageUri
    )

    fun mapListDbModelToListEntity(listDbModel: List<ImageEntityDbModel>) = listDbModel.map {
        mapDbModelToEntity(it)
    }
}