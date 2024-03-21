package com.test.xone.presentation

import android.app.Application
import com.test.xone.di.DaggerApplicationComponent

class MyApplication : Application() {
    val component by lazy {
        DaggerApplicationComponent.factory()
            .create(this)
    }
}