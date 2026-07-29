
package com.example.kmmexlvistech.presentation.ui.leads

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.datasource.dto.LeadDto
import com.example.kmmexlvistech.presentation.theme.DashboardBackground
import com.example.kmmexlvistech.presentation.ui.common.DropdownField
import com.example.kmmexlvistech.presentation.ui.common.DropdownOption
import com.example.kmmexlvistech.presentation.ui.common.FormTextField
import com.example.kmmexlvistech.presentation.ui.common.RequiredLabel
import com.example.kmmexlvistech.presentation.ui.common.SectionHeader
import com.example.kmmexlvistech.presentation.ui.common.ToggleRow
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen
import com.example.kmmexlvistech.presentation.ui.leads.viewmodel.LeadsEvent
import com.example.kmmexlvistech.presentation.ui.leads.viewmodel.LeadsState
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLeadScreen(
    state: LeadsState,
    events: (LeadsEvent) -> Unit,
    token: String,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
) {
    LaunchedEffect(state.leadSaved) {
        if (state.leadSaved) onSaved()
    }


     val userOptions = listOf(
        DropdownOption(id = 1021, label = "Aarif Tadavi"),
        DropdownOption(id = 1022, label = "Bushara Shidhiki"),
        DropdownOption(id = 1023, label = "Rohan Mehta"),
    )

     val campaignOptions = listOf(
        DropdownOption(id = 1, label = "Summer Sale 2026"),
        DropdownOption(id = 2, label = "Diwali Offer"),
        DropdownOption(id = 3, label = "New Customer Referral"),
    )

    DefaultUiScreen(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = { events(LeadsEvent.OnRemoveHeadFromQueue) },
        progressBarState = if (state.isSavingLead) ProgressBarState.Loading else ProgressBarState.Idle,
    ) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Create Lead", color = Color.White, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = { events(LeadsEvent.SaveLead(token)) }) {
                        Text("Save", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E2A5E))
            )

            Column(
                Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(Color(0xFFF5F6FA))
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SectionHeader(Icons.Default.Info, "Basic Information")

                RequiredLabel("First Name")
                FormTextField(state.firstName, { events(LeadsEvent.FirstNameChanged(it)) }, "Enter first name", Icons.Default.Person)

                RequiredLabel("Last Name")
                FormTextField(state.lastName, { events(LeadsEvent.LastNameChanged(it)) }, "Enter last name", Icons.Default.Person)

                RequiredLabel("Company Name")
                FormTextField(state.companyName, { events(LeadsEvent.CompanyNameChanged(it)) }, "Enter company name", Icons.Default.Business)

                RequiredLabel("Mobile")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        Modifier
                            .width(80.dp)
                            .background(Color.White, RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 14.dp)
                    ) {
                        Text(state.countryCode, fontWeight = FontWeight.Medium)
                    }
                    FormTextField(
                        value = state.mobile,
                        onValueChange = { events(LeadsEvent.MobileChanged(it)) },
                        placeholder = "Enter mobile number",
                        icon = Icons.Default.Call,
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Phone
                    )
                }

                Label("Phone")
                FormTextField(state.phone, { events(LeadsEvent.PhoneChanged(it)) }, "Enter phone number", Icons.Default.Call, keyboardType = KeyboardType.Phone)

                Label("Email")
                FormTextField(state.email, { events(LeadsEvent.EmailChanged(it)) }, "Enter email address", Icons.Default.Email, keyboardType = KeyboardType.Email)

                Spacer(Modifier.height(12.dp))
                SectionHeader(Icons.Default.Business, "Lead Information")

                RequiredLabel("Lead Source")
                DropdownField(
                    label = "Select lead source",
                    icon = Icons.Default.Person,
                    options = state.leadSourceOptions,
                    selectedId = state.leadSourceId,
                    onSelected = { events(LeadsEvent.LeadSourceChanged(it)) }
                )

                RequiredLabel("Lead Status")
                DropdownField(
                    label = "Select lead status",
                    icon = Icons.Default.Assignment,
                    options = state.leadStatusOptions,   // ✅ from API
                    selectedId = state.leadStatusId,
                    onSelected = { events(LeadsEvent.LeadStatusChanged(it)) }
                )

                RequiredLabel("Lead Owner")
                DropdownField(
                    label = "Select lead owner",
                    icon = Icons.Default.Person,
                    options = userOptions,               // static stub for now
                    selectedId = state.leadOwnerId,
                    onSelected = { events(LeadsEvent.LeadOwnerChanged(it)) }
                )

                RequiredLabel("Assigned To")
                DropdownField(
                    label = "Select user",
                    icon = Icons.Default.Person,
                    options = userOptions,               // static stub for now
                    selectedId = state.assignedToId,
                    onSelected = { events(LeadsEvent.AssignedToChanged(it)) }
                )


                Label("Campaign (Optional)")
                DropdownField(
                    label = "Select campaign",
                    icon = Icons.Default.Campaign,
                    options = campaignOptions,           // static stub for now
                    selectedId = state.campaignId,
                    onSelected = { events(LeadsEvent.CampaignChanged(it)) }
                )

                Spacer(Modifier.height(8.dp))
                ToggleRow("Is Active", state.isActive) { events(LeadsEvent.IsActiveChanged(it)) }
                ToggleRow("Is Converted", state.isConverted) { events(LeadsEvent.IsConvertedChanged(it)) }

                Spacer(Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("Cancel", color = Color(0xFF1E2A5E)) }

                    Button(
                        onClick = { events(LeadsEvent.SaveLead(token)) },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B5BFF))
                    ) { Text("Save Lead", color = Color.White, fontWeight = FontWeight.SemiBold) }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun Label(text: String) {
    Text(text, fontWeight = FontWeight.Medium, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
}

