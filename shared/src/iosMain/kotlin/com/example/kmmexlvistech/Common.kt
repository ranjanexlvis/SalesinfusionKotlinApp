package com.example.kmmexlvistech

import platform.UIKit.UIApplication
import platform.darwin.NSObject


actual typealias Context = NSObject
actual object AppContext {
    actual fun get(): Context {
        return UIApplication.sharedApplication
    }
}