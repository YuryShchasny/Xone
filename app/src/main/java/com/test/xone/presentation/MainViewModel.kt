package com.test.xone.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.xone.domain.AddImageEntityUseCase
import com.test.xone.domain.DeleteImageEntityUseCase
import com.test.xone.domain.GetImageEntityListUseCase
import com.test.xone.domain.ImageEntity
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getImageEntityListUseCase: GetImageEntityListUseCase,
    private val addImageEntityUseCase: AddImageEntityUseCase,
    private val deleteImageEntityUseCase: DeleteImageEntityUseCase
) : ViewModel() {

    private var _recyclerViewDeleteMode = MutableLiveData<Boolean>()
    val recyclerViewDeleteMode : LiveData<Boolean> get() = _recyclerViewDeleteMode

    val imageList = getImageEntityListUseCase()

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

    fun setRecyclerViewMode(isDeleteMode: Boolean) {
        _recyclerViewDeleteMode.value = isDeleteMode
    }

}