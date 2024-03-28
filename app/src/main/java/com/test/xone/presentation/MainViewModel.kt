package com.test.xone.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.xone.domain.AddImageEntityUseCase
import com.test.xone.domain.DeleteImageEntityUseCase
import com.test.xone.domain.GetImageEntityListUseCase
import com.test.xone.domain.ImageEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    getImageEntityListUseCase: GetImageEntityListUseCase,
    private val addImageEntityUseCase: AddImageEntityUseCase,
    private val deleteImageEntityUseCase: DeleteImageEntityUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    val list = getImageEntityListUseCase()

    fun addImage(imageUri: String) {
        viewModelScope.launch {
            addImageEntityUseCase(ImageEntity(imageUri))
        }
    }

    fun deleteImage(imageEntity: ImageEntity) {
        viewModelScope.launch {
            deleteImageEntityUseCase(imageEntity)
        }
    }

    fun setDeleteState(list: List<ImageEntity>) {
        _state.value = State.Deleting(list)
    }

    fun setLoadingState() {
        _state.value = State.Loading
    }

    fun setContentState(list: List<ImageEntity>) {
        _state.value = State.Content(list)
    }



}