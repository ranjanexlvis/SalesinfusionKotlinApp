package com.example.kmmexlvistech.presentation.ui.profile.viewmodel

import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.models.ProfileDto

// presentation/ui/profile/viewmodel/ProfileState.kt
data class PickerOption(val key: String, val label: String)

data class ProfileState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val errorQueue: Queue<UIComponent> = Queue(mutableListOf()),

    val profile: ProfileDto? = null,
    val isLoading: Boolean = false,
    val loadError: String? = null,

    // edit form
    val firstName: String = "",
    val lastName: String = "",
    val emailId: String = "",
    val mobileNumber: String = "",
    val languageCode: String = "",
    val currencyId: Int? = null,
    val timeZoneId: Int? = null,
    val languageOptions: List<PickerOption> = emptyList(),
    val currencyOptions: List<PickerOption> = emptyList(),
    val timeZoneOptions: List<PickerOption> = emptyList(),
    val showValidation: Boolean = false,

    val isSaving: Boolean = false,
    val saveError: String? = null,
    val profileSaved: Boolean = false,
)