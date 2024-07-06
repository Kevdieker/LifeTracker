package com.kevker.lifetracker


import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kevker.lifetracker.navigation.Navigation
import com.kevker.lifetracker.ui.theme.LifeTrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request SCHEDULE_EXACT_ALARM permission on Android 12 and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!getSystemService(AlarmManager::class.java).canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }

        setContent {
            LifeTrackerTheme {
                Navigation()
            }
        }
    }
}
