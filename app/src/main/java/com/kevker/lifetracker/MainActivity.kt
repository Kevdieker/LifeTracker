package com.kevker.lifetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kevker.lifetracker.navigation.Navigation
import com.kevker.lifetracker.ui.theme.LifeTrackerTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LifeTrackerTheme {
                Navigation()
            }
        }

    }
}
