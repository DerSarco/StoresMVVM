package com.cursosant.android.stores

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cursosant.android.stores.common.database.StoreAPI
import com.cursosant.android.stores.common.database.StoreDatabase

/****
 * Project: Stores
 * From: com.cursosant.android.stores
 * Created by Alain Nicolás Tello on 27/11/20 at 12:40
 * Course: Android Practical with Kotlin from zero.
 * All rights reserved 2021.
 *
 * All my Courses(Only on Udemy):
 * https://www.udemy.com/user/alain-nicolas-tello/
 ***/
class StoreApplication : Application() {
    companion object{
        lateinit var database: StoreDatabase
        lateinit var storeAPI: StoreAPI
    }

    override fun onCreate() {
        super.onCreate()

        val MIGRATION_1_2 = object : Migration(1, 2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE StoreEntity ADD COLUMN photoUrl TEXT NOT NULL DEFAULT ''")
            }
        }

        database = Room.databaseBuilder(this,
            StoreDatabase::class.java,
            "StoreDatabase")
            .addMigrations(MIGRATION_1_2)
            .build()

        //Volley
        storeAPI = StoreAPI.getInstance(this)
    }
}