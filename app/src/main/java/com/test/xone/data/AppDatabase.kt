package com.test.xone.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ImageEntityDbModel::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageEntityDao(): ImageEntityDao
}