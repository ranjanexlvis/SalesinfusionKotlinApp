package com.example.kmmexlvistech.presentation.ui.common


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
 fun StepLabel(step: Int, title: String, subtitle: String?) {
    Row(verticalAlignment = Alignment.Top) {
        Box(Modifier.size(20.dp).background(Color(0xFF3B5BFF), CircleShape), contentAlignment = Alignment.Center) {
            Text(step.toString(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(8.dp))
        Column {
            Row {
                Text(title, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                Text(" *", color = Color.Red, fontSize = 13.sp)
            }
            subtitle?.let { Text(it, color = Color.Gray, fontSize = 11.sp) }
        }
    }
}

@Composable
 fun RecordTypeChip(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .background(if (selected) Color(0xFFEDEBFF) else Color.White, RoundedCornerShape(10.dp))
            .border(1.dp, if (selected) Color(0xFF7C5CFC) else Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = if (selected) Color(0xFF7C5CFC) else Color.Gray, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, color = if (selected) Color(0xFF7C5CFC) else Color.DarkGray, fontSize = 13.sp)
    }
}

@Composable
 fun DateTimeRow(date: String, time: String, onDateChange: (String) -> Unit, onTimeChange: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = date, onValueChange = onDateChange, readOnly = true,
            placeholder = { Text("Select date", color = Color.Gray, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp)) },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE5E7EB)),
            modifier = Modifier.weight(1.3f)
        )
        OutlinedTextField(
            value = time, onValueChange = onTimeChange, readOnly = true,
            trailingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp)) },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE5E7EB)),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
 fun SectionHeader(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
        Icon(icon, contentDescription = null, tint = Color(0xFF3B5BFF), modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF3B5BFF), fontSize = 15.sp)
    }
}

@Composable
 fun RequiredLabel(text: String) {
    Row(Modifier.padding(top = 8.dp, bottom = 4.dp)) {
        Text(text, fontWeight = FontWeight.Medium, fontSize = 13.sp)
        Text(" *", color = Color.Red, fontSize = 13.sp)
    }
}

@Composable
 fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp)) },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedBorderColor = Color(0xFFE5E7EB),
        ),
        modifier = modifier.fillMaxWidth()
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
 fun DropdownField(
    label: String,
    icon: ImageVector,
    options: List<DropdownOption>,
    selectedId: Int?,
    onSelected: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.id == selectedId }?.label

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selectedLabel ?: "",
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(label, color = Color.Gray, fontSize = 14.sp) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color(0xFFE5E7EB),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true) // <-- key line: wires touch input correctly
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onSelected(option.id)
                        expanded = false
                    }
                )
            }
        }
    }
}






data class DropdownOption(val id: Int, val label: String)



@Composable
 fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontWeight = FontWeight.Medium)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF3B5BFF))
        )
    }
}

@Composable
fun RecordTypeChipColored(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .background(if (selected) color.copy(alpha = 0.1f) else Color.White, RoundedCornerShape(10.dp))
            .border(1.dp, if (selected) color else Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = if (selected) color else Color.Gray, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, color = if (selected) color else Color.DarkGray, fontSize = 13.sp)
    }
}

