package com.test.xone.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ImageEntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(imageEntityDbModel: ImageEntityDbModel)

    @Query("SELECT * FROM images")
    fun getAllImages(): LiveData<List<ImageEntityDbModel>>

    @Delete
    suspend fun deleteImage(imageEntityDbModel: ImageEntityDbModel)
}