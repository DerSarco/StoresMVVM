package com.cursosant.android.stores.mainModule.model

import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.cursosant.android.stores.StoreApplication
import com.cursosant.android.stores.common.entities.StoreEntity
import com.cursosant.android.stores.common.utils.Constants
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainInteractor {


    fun getStores(callback: (MutableList<StoreEntity>) -> Unit){
        val url = Constants.STORES_URL + Constants.GET_ALL_PATH
        var storeList = mutableListOf<StoreEntity>()
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, { response ->

//            val status = response.getInt(Constants.STATUS_PROPERTY)
            val status = response.optInt(Constants.STATUS_PROPERTY, Constants.ERROR)

            if(status == Constants.SUCCESS){
                val jsonListOPT = response.optJSONArray(Constants.STORES_PROPERTY)
                val jsonList = jsonListOPT?.toString() ?: "[]"
                val mutableListType = object : TypeToken<MutableList<StoreEntity>>(){}.type
                storeList = Gson().fromJson(jsonList, mutableListType)
                callback(storeList)
                return@JsonObjectRequest
            }
            callback(storeList)
        }, {
            it.printStackTrace()
            callback(storeList)
        })

        StoreApplication.storeAPI.addToRequestQueue(jsonObjectRequest)

    }

    fun getStoresRoom(callback: (MutableList<StoreEntity>) -> Unit ){
        doAsync {
            val storeList = StoreApplication.database.storeDao().getAllStores()
            uiThread {
                val json = Gson().toJson(storeList)
                Log.i("GSON", json)
                callback(storeList)
            }
        }
    }

    fun deleteStore(storeEntity: StoreEntity, callback: (StoreEntity) -> Unit){
        doAsync {
            StoreApplication.database.storeDao().deleteStore(storeEntity)
            uiThread {
                callback(storeEntity)
            }
        }

    }

    fun updateStore(storeEntity: StoreEntity, callback: (StoreEntity) -> Unit){
        storeEntity.isFavorite = !storeEntity.isFavorite
        doAsync {
            StoreApplication.database.storeDao().updateStore(storeEntity)
            uiThread {
                callback(storeEntity)
            }
        }
    }

}