package com.kevker.lifetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.kevker.lifetracker.data.LTDatabase
import com.kevker.lifetracker.factories.ViewModelFactory
import com.kevker.lifetracker.navigation.Navigation
import com.kevker.lifetracker.repositories.StepRepository
import com.kevker.lifetracker.handlers.PermissionHandler

import com.kevker.lifetracker.ui.theme.LifeTrackerTheme
import com.kevker.lifetracker.viewmodels.HomeScreenViewModel


class MainActivity : ComponentActivity() {

    private lateinit var viewModel: HomeScreenViewModel
    private lateinit var permissionHandler: PermissionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionHandler = PermissionHandler(this)

        val activityRecognitionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    viewModel.startStepSensor()
                } else {
                    showPermissionDeniedMessage()
                }
            }

        permissionHandler.setPermissionLauncher(PermissionHandler.ACTIVITY_RECOGNITION_PERMISSION, activityRecognitionLauncher)

        val db = LTDatabase.getDatabase(this)
        val stepRepository = StepRepository.getInstance(db.stepCountDao())
        val viewModelFactory = ViewModelFactory(
            context = this,
            stepRepository = stepRepository,
            permissionHandler = permissionHandler
        )
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeScreenViewModel::class.java)

        setContent {
            LifeTrackerTheme {
                Navigation()
            }
        }

        viewModel.isPermissionGranted.observe(this) { isGranted ->
            if (isGranted) {
                viewModel.startStepSensor()
            } else {
                viewModel.requestPermission()
            }
        }
    }

    private fun showPermissionDeniedMessage() {
        // Show a message to the user
    }
}
