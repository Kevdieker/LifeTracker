package com.kevker.lifetracker.widget

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.kevker.lifetracker.models.Glass

import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@Composable
fun GlassComposable(glass: Glass, onLongPress: (Glass) -> Unit, onDrag: (Glass) -> Unit) {
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Cyan)
            .padding(16.dp)
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        onDrag(glass)
                        offset = Offset.Zero
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offset = Offset(offset.x + dragAmount.x, offset.y + dragAmount.y)
                    }
                )
            }
    ) {
        Text(
            text = "${glass.ml} ml",
            color = Color.Blue,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}





