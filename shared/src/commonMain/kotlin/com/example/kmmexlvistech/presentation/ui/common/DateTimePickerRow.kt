package com.example.kmmexlvistech.presentation.ui.common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color




import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.presentation.ui.calls.viewmodel.millisToDisplayDate
import com.example.kmmexlvistech.presentation.ui.calls.viewmodel.millisToDisplayTime
import kotlinx.datetime.Clock
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
 fun DateTimePickerRow(
    dateTimeMillis: Long?,
    onDateTimeChanged: (Long) -> Unit,
    accentColor: Color = Color(0xFF16A34A),
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var pendingDateMillis by remember { mutableStateOf<Long?>(null) }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = millisToDisplayDate(dateTimeMillis),
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Select date", color = Color.Gray, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp)) },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE5E7EB)),
            modifier = Modifier
                .weight(1.3f)
                .clickable { showDatePicker = true }
        )
        OutlinedTextField(
            value = millisToDisplayTime(dateTimeMillis),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp)) },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE5E7EB)),
            modifier = Modifier
                .weight(1f)
                .clickable {
                    // if no date chosen yet, seed time-picker flow off "now"
                    pendingDateMillis = dateTimeMillis ?: Clock.System.now().toEpochMilliseconds()
                    showTimePicker = true
                }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateTimeMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { pendingDateMillis = it }
                    showDatePicker = false
                    showTimePicker = true // chain straight into time picker
                }) { Text("Next", color = accentColor) }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val baseMillis = pendingDateMillis ?: dateTimeMillis ?: Clock.System.now().toEpochMilliseconds()
        val baseDt = kotlinx.datetime.Instant.fromEpochMilliseconds(baseMillis)
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
        val timePickerState = rememberTimePickerState(initialHour = baseDt.hour, initialMinute = baseDt.minute, is24Hour = false)

        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val combined = kotlinx.datetime.LocalDateTime(
                        baseDt.year, baseDt.monthNumber, baseDt.dayOfMonth,
                        timePickerState.hour, timePickerState.minute
                    )
                    val resultMillis = combined
                        .toInstant(kotlinx.datetime.TimeZone.currentSystemDefault())
                        .toEpochMilliseconds()
                    onDateTimeChanged(resultMillis)
                    showTimePicker = false
                }) { Text("OK", color = accentColor) }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancel") } },
            text = { TimePicker(state = timePickerState) }
        )
    }
}