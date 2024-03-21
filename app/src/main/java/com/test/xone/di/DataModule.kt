package com.test.xone.di

import android.app.Application
import androidx.room.Room
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.test.xone.data.AppDatabase
import com.test.xone.data.AppRepositoryImpl
import com.test.xone.data.ImageEntityDao
import com.test.xone.domain.AppRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @Binds
    @ApplicationScope
    fun bindAppRepository(impl: AppRepositoryImpl) : AppRepository

    companion object {
        @Provides
        @ApplicationScope
        fun provideImageEntityDao(database: AppDatabase): ImageEntityDao {
            return database.imageEntityDao()
        }

        @Provides
        @ApplicationScope
        fun provideAppDatabase(application: Application): AppDatabase {
            return Room.databaseBuilder(
                application,
                AppDatabase::class.java,
                "application_xone.db"
            ).build()
        }
        @Provides
        @ApplicationScope
        fun provideFireBaseStorage(application: Application) : FirebaseStorage {
            FirebaseApp.initializeApp(application)
            return Firebase.storage
        }
    }
}