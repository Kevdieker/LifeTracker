package com.kevker.lifetracker.widget

import android.view.MotionEvent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.kevker.lifetracker.models.Glass

import androidx.compose.ui.input.pointer.pointerInteropFilter


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Waterjug(
    sliderPosition: Float,
    percentage: Int,
    isDraggingOver: Boolean,
    onDragStarted: () -> Unit,
    onDragEnded: () -> Unit,
    onDrop: (Glass) -> Unit,
    onLongPress: () -> Unit
) {
    val waterHeight by animateDpAsState(targetValue = 200.dp * sliderPosition)

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .size(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isDraggingOver) Color.LightGray else Color.Gray)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onLongPress()
                    }
                )
            }
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_MOVE -> {
                        onDragStarted()
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        onDragEnded()
                        true
                    }
                    else -> false
                }
            }
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
}


