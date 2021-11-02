package com.cursosant.android.stores.common.database

import androidx.room.*
import com.cursosant.android.stores.common.entities.StoreEntity

/****
 * Project: Stores
 * From: com.cursosant.android.stores
 * Created by Alain Nicolás Tello on 27/11/20 at 12:24
 * Course: Android Practical with Kotlin from zero.
 * All rights reserved 2021.
 *
 * All my Courses(Only on Udemy):
 * https://www.udemy.com/user/alain-nicolas-tello/
 ***/
@Dao
interface StoreDao {
    @Query("SELECT * FROM StoreEntity")
    fun getAllStores() : MutableList<StoreEntity>

    @Query("SELECT * FROM StoreEntity where id = :id")
    fun getStoreById(id: Long): StoreEntity

    @Insert
    fun addStore(storeEntity: StoreEntity): Long

    @Update
    fun updateStore(storeEntity: StoreEntity)

    @Delete
    fun deleteStore(storeEntity: StoreEntity)
}