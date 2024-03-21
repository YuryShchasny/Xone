package com.test.xone.domain

import javax.inject.Inject

class AddImageEntityUseCase @Inject constructor(private val appRepository: AppRepository) {
    suspend operator fun invoke(imageEntity: ImageEntity) {
        appRepository.addImage(imageEntity)
    }
}