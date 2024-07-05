package com.kevker.lifetracker.sampledata

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

class PermissionHandler(private val context: Context) {

    private var permissionLaunchers: MutableMap<String, ActivityResultLauncher<String>> = mutableMapOf()

    fun setPermissionLauncher(permission: String, launcher: ActivityResultLauncher<String>) {
        permissionLaunchers[permission] = launcher
    }

    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(permission: String) {
        permissionLaunchers[permission]?.launch(permission)
    }

    companion object {
        const val ACTIVITY_RECOGNITION_PERMISSION = Manifest.permission.ACTIVITY_RECOGNITION
    }
}
