package com.kevker.lifetracker.widget

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Waterjug(
    sliderPosition: Float,
    percentage: Int
) {
    val waterHeight by animateDpAsState(targetValue = 200.dp * sliderPosition)

    Column {
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Gray)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(waterHeight)
                    .background(Color.Blue)
            ) {
                Text(
                    text = "$percentage%",
                    color = Color.Cyan,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}