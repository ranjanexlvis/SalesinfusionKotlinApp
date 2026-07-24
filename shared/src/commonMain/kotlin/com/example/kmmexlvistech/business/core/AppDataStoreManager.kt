package com.example.kmmexlvistech.business.core


import com.example.kmmexlvistech.AppContext
import com.example.kmmexlvistech.common.deleteData
import com.example.kmmexlvistech.common.getData
import com.example.kmmexlvistech.common.putData


const val APP_DATASTORE = "com.exlvis.Kmm"

class AppDataStoreManager (val context : AppContext) :
   AppDataStore {
    override suspend fun setValue(key: String, value: String) {
        context.get().putData(key, value)
    }

    override suspend fun readValue(key: String): String? {
        return context.get().getData(key)
    }

    override suspend fun deleteValues() {
        return context.get().deleteData()
    }

}