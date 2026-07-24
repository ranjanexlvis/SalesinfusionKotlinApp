package com.example.kmmexlvistech.common

// Android-specific implementation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import android.os.Build


actual object AppUpdateHelper {

    private val context: Context
        get() = ApplicationContextHolder.context

    actual fun getCurrentAppVersion(): String {
        return try {
            val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toString()
            } else {
                packageInfo.versionCode.toString()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }


    actual fun openPlayStoreForUpdate() {
        val packageName = context.packageName
        try {
            // Intent for the Play Store app
            val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=$packageName")
                setPackage("com.android.vending") // Direct intent to Play Store app
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Required for non-Activity context
            }
            context.startActivity(playStoreIntent)
        } catch (e: Exception) {
            // Fallback to the browser if Play Store is unavailable
            val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Required for non-Activity context
            }
            context.startActivity(browserIntent)
        }
    }




}


@SuppressLint("StaticFieldLeak")
object ApplicationContextHolder {
    private var _context: Context? = null

    val context: Context
        get() = _context ?: throw IllegalStateException("Application context not initialized!")

    fun init(context: Context) {
        if (_context == null) { // Ensure initialization only once
            _context = context.applicationContext as Context?
        }
    }
}

