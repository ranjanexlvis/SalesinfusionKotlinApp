package com.example.kmmexlvistech.common

import com.example.kmmexlvistech.Context
import kotlinx.coroutines.flow.MutableSharedFlow
import platform.Foundation.NSUserDefaults


actual suspend fun Context.putData(key: String, `object`: String) {
    val sharedFlow = MutableSharedFlow<String>()
    NSUserDefaults.standardUserDefaults().setObject(`object`, key)
    sharedFlow.emit(`object`)
}

actual suspend inline fun Context.getData(key: String): String? {
    return NSUserDefaults.standardUserDefaults().stringForKey(key)
}

actual suspend inline fun Context.deleteData() {

}
