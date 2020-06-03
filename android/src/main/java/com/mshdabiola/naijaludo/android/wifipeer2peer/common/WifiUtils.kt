package com.mshdabiola.naijaludo.wifipeer2peer.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat

object WifiUtils {
    const val REQUEST_PERMISSION = 1001

    private val runtimePermissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION
    )

    fun checkWifiSupport(context: Context): Boolean {
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI)) {
            Log.w("WifiUtils", "Wifi is not supported")
            return false
        }

        return true
    }

    fun checkWifiDirectSupport(context: Context): Boolean {
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)) {
            Log.w("WifiUtils", "Wifi direct is not supported")
            return false
        }

        return true
    }

    fun hasPermissions(context: Context): Boolean {
        return !runtimePermissions.any {
            ActivityCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
    }

    fun permissionsToAsk(context: Context): Array<String> {
        return runtimePermissions.filter {
            ActivityCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
    }

    fun askPermissions(context: Context) {
        if (!hasPermissions(context)) {
            permissionsToAsk(context)
        }
    }
}