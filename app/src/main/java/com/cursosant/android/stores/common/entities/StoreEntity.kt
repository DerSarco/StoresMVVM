package com.cursosant.android.stores.common.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/****
 * Project: Stores
 * From: com.cursosant.android.stores
 * Created by Alain Nicolás Tello on 26/11/20 at 13:19
 * Course: Android Practical with Kotlin from zero.
 * All rights reserved 2021.
 *
 * All my Courses(Only on Udemy):
 * https://www.udemy.com/user/alain-nicolas-tello/
 ***/
@Entity(tableName = "StoreEntity")
data class StoreEntity(@PrimaryKey(autoGenerate = true) var id: Long = 0,
                       var name: String,
                       var phone: String,
                       var website: String = "",
                       var photoUrl: String,
                       var isFavorite: Boolean = false){

    constructor(): this(name = "", phone = "", photoUrl = "")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StoreEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
