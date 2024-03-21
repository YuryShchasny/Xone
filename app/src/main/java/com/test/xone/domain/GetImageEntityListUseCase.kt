package com.test.xone.domain

import androidx.lifecycle.LiveData
import javax.inject.Inject

class GetImageEntityListUseCase @Inject constructor(private val appRepository: AppRepository) {
    operator fun invoke(): LiveData<List<ImageEntity>> = appRepository.getImageList()
}