package com.kevker.lifetracker.widget

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.Calendar

@Composable
fun SetSleepTimeDialog(
    onDismiss: () -> Unit,
    onTimeSet: (Int, Int) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        TimePickerDialog(context, { _, selectedHour: Int, selectedMinute: Int ->
            onTimeSet(selectedHour, selectedMinute)
            showDialog.value = false
        }, hour, minute, true).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Sleep Time Reminder") },
        text = {
            Column {
                Button(onClick = { showDialog.value = true }) {
                    Text("Choose Time")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onCancel) {
                    Text("Cancel Reminder")
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
