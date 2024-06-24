package com.kevker.lifetracker.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.kevker.lifetracker.models.Glass

@Composable
fun GlassList(glasses: List<Glass>, onAddGlass: (Int) -> Unit, onDeleteGlass: (Glass) -> Unit, onDrag: (Glass) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyRow {
            items(glasses) { glass ->
                GlassComposable(
                    glass = glass,
                    onLongPress = onDeleteGlass,
                    onDrag = onDrag
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onAddGlass(-1) }) { // Pass -1 to indicate the add glass button
            Text("Add Glass")
        }
    }
}




