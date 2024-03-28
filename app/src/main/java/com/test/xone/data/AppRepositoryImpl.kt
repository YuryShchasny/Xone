package com.test.xone.data

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.test.xone.domain.AppRepository
import com.test.xone.domain.ImageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject


class AppRepositoryImpl @Inject constructor(
    private val imageEntityDao: ImageEntityDao,
    private val imageEntityMapper: ImageEntityMapper,
    private val storage: FirebaseStorage
) : AppRepository {

    override suspend fun addImage(imageEntity: ImageEntity) {
        imageEntityDao.insertImage(imageEntityMapper.mapEntityToDbModel(imageEntity))
        uploadImageToStorage(imageEntity)
    }

    override suspend fun deleteImage(imageEntity: ImageEntity) {
        imageEntityDao.deleteImage(imageEntityMapper.mapEntityToDbModel(imageEntity))
        deleteImageInStorage(imageEntity)
    }

    override fun getImageList(): Flow<List<ImageEntity>> {
        return imageEntityDao.getAllImages().map {
            imageEntityMapper.mapListDbModelToListEntity(it)
        }
    }

    private fun uploadImageToStorage(imageEntity: ImageEntity) {
        val storageRef = storage.reference
        val name = File(imageEntity.imageUri).name
        val imageRef = storageRef.child("images/$name")
        val uploadTask = imageRef.putFile(Uri.parse(imageEntity.imageUri))
        uploadTask.addOnSuccessListener {
            Log.d("Firebase Storage", "Image upload successful")
        }.addOnFailureListener {
            Log.d("Firebase Storage", "Failed to load image to Firebase storage")
        }
    }

    private fun deleteImageInStorage(imageEntity: ImageEntity) {
        val storageRef = storage.reference
        val name = File(imageEntity.imageUri).name
        val imageRef = storageRef.child("images/$name")
        imageRef.delete().addOnSuccessListener {
            Log.d("Firebase Storage", "Image delete successful")
        }.addOnFailureListener {
                Log.d("Firebase Storage", "Failed to delete image in Firebase storage")
            }
    }

}