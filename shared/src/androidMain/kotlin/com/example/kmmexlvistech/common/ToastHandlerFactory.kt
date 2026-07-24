// androidMain
package com.example.kmmexlvistech.common

import android.content.Context
import android.widget.Toast


actual object ToastHandlerFactory {
    private lateinit var applicationContext: Context

    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }

    actual fun create(): ToastHandler {
        if (!::applicationContext.isInitialized) {
            throw IllegalStateException("ToastHandlerFactory is not initialized. Call initialize(context) first.")
        }
        return AndroidToastHandler(applicationContext)
    }
}

class AndroidToastHandler(private val context: Context) : ToastHandler {
    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}


