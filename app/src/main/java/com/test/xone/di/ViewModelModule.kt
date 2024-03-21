package com.test.xone.di

import androidx.lifecycle.ViewModel
import com.test.xone.presentation.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun bindScheduleMainViewModel(viewModel: MainViewModel): ViewModel

}