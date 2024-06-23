package com.kevker.lifetracker.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kevker.lifetracker.viewmodels.HydrationViewModel
import com.kevker.lifetracker.widget.SimpleBottomAppBar
import com.kevker.lifetracker.widget.SimpleTopAppBar

@Composable
fun HydrationScreen(navController: NavController) {
    val viewModel: HydrationViewModel = viewModel()

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = "Hydration Tracker",
                onNavigationIconClick = null
            )
        },
        bottomBar = { SimpleBottomAppBar(navController) }
    ) { innerPadding ->
        Waterjug(
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
fun Waterjug(
    modifier: Modifier,
) {
    var sliderPosition by remember { mutableStateOf(0.5f) }
    val waterHeight by animateDpAsState(targetValue = 200.dp * sliderPosition)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Gray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(waterHeight)
                    .background(Color.Blue.copy(alpha = 0.5f))
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
