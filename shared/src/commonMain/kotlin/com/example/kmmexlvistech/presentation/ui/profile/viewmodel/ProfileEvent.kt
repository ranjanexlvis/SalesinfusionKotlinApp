package com.example.kmmexlvistech.presentation.ui.profile.viewmodel


// presentation/ui/profile/viewmodel/ProfileEvent.kt
sealed class ProfileEvent {
    data class GetProfile(val token: String) : ProfileEvent()
    object LoadProfileForEdit : ProfileEvent()
    data class FirstNameChanged(val value: String) : ProfileEvent()
    data class LastNameChanged(val value: String) : ProfileEvent()
    data class EmailChanged(val value: String) : ProfileEvent()
    data class MobileChanged(val value: String) : ProfileEvent()
    data class LanguageChanged(val code: String) : ProfileEvent()
    data class CurrencyChanged(val id: Int) : ProfileEvent()
    data class TimeZoneChanged(val id: Int) : ProfileEvent()
    data class SaveProfile(val token: String) : ProfileEvent()
    object ResetProfileSaved : ProfileEvent()
    object OnRemoveHeadFromQueue : ProfileEvent()
}