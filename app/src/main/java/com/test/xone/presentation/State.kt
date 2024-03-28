package com.test.xone.presentation

import com.test.xone.domain.ImageEntity

sealed class State {
    data object Loading : State()
    data class Deleting(val currencyList: List<ImageEntity>) : State()
    data class Content(val currencyList: List<ImageEntity>) : State()
}