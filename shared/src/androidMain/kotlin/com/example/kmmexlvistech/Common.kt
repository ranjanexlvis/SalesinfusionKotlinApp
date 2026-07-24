package com.example.kmmexlvistech

import android.app.Application
import java.lang.ref.WeakReference

actual typealias Context = Application

actual object AppContext {
    private var value: WeakReference<Context?>? = null

    fun set(context: Context) {
        value = WeakReference(context)
    }

    actual fun get(): Context {
        return value?.get() ?: throw RuntimeException("Context not set!")
    }
}