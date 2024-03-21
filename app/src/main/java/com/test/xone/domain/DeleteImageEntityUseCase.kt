package com.test.xone.domain

import javax.inject.Inject

class DeleteImageEntityUseCase @Inject constructor(private val appRepository: AppRepository) {
    suspend operator fun invoke(imageEntity: ImageEntity) {
        appRepository.deleteImage(imageEntity)
    }
}