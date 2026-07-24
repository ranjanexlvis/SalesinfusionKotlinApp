package com.example.kmmexlvistech.common

import com.example.kmmexlvistech.Context


expect suspend fun Context.putData(key: String, `object`: String)

expect suspend fun Context.getData(key: String): String?
expect suspend fun Context.deleteData()

