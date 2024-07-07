package com.kevker.lifetracker

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.kevker.lifetracker.navigation.Navigation
import com.kevker.lifetracker.ui.theme.LifeTrackerTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.forEach { (permission, isGranted) ->
                when (permission) {
                    Manifest.permission.ACTIVITY_RECOGNITION -> {
                        if (isGranted) {
                            // Permission granted, proceed with step tracking
                        } else {
                            // Permission denied, handle accordingly
                        }
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request SCHEDULE_EXACT_ALARM permission on Android 12 and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!getSystemService(AlarmManager::class.java).canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }

        // Check and request ACTIVITY_RECOGNITION permission at runtime for Android 10 and above
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionsLauncher.launch(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION))
        }

        setContent {
            LifeTrackerTheme {
                Navigation()
            }
        }
    }
}
