package com.test.xone.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetImageEntityListUseCase @Inject constructor(private val appRepository: AppRepository) {
    operator fun invoke(): Flow<List<ImageEntity>> = appRepository.getImageList()
}