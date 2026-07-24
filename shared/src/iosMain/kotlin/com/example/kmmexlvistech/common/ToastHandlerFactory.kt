package com.example.kmmexlvistech.common

import com.example.kmmexlvistech.common.ToastHandler
import platform.UIKit.UIAlertControllerStyleAlert

import platform.UIKit.UIViewController
import platform.UIKit.UIAlertController

import platform.UIKit.*
import platform.Foundation.*

actual object ToastHandlerFactory {
    actual fun create(): ToastHandler {
        val rootViewController = UIApplication.sharedApplication
            .keyWindow?.rootViewController
            ?: throw IllegalStateException("No root view controller found")

        return IOSToastHandler(rootViewController)
    }
}

class IOSToastHandler(private val viewController: UIViewController) : ToastHandler {
    override fun showToast(message: String) {
        val alert = UIAlertController.alertControllerWithTitle(
            title = null,
            message = message,
            preferredStyle = UIAlertControllerStyleAlert
        )

        viewController.presentViewController(alert, animated = true, completion = null)

        // Use NSTimer to dismiss the alert after 2 seconds
        NSTimer.scheduledTimerWithTimeInterval(
            2.0, // 2 seconds
            repeats = false
        ) { _ ->
            alert.dismissViewControllerAnimated(true, completion = null)
        }
    }
}

