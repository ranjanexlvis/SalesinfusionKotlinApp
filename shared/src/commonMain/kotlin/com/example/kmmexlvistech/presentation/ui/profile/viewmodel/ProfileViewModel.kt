package com.example.kmmexlvistech.presentation.ui.profile.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.kmmexlvistech.business.core.DataState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.business.datasource.models.UpdateProfileRequestDto
import com.example.kmmexlvistech.business.repos.ProfileRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope


// presentation/ui/profile/viewmodel/ProfileViewModel.kt
class ProfileViewModel(private val profileRepository: ProfileRepository) : ViewModel() {
    val state: MutableState<ProfileState> = mutableStateOf(ProfileState())

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.GetProfile -> loadProfile(event.token)
            is ProfileEvent.LoadProfileForEdit -> loadProfileForEdit()
            is ProfileEvent.FirstNameChanged -> state.value = state.value.copy(firstName = event.value)
            is ProfileEvent.LastNameChanged -> state.value = state.value.copy(lastName = event.value)
            is ProfileEvent.EmailChanged -> state.value = state.value.copy(emailId = event.value)
            is ProfileEvent.MobileChanged -> state.value =
                state.value.copy(mobileNumber = event.value.filter { it.isDigit() }.take(15))
            is ProfileEvent.LanguageChanged -> state.value = state.value.copy(languageCode = event.code)
            is ProfileEvent.CurrencyChanged -> state.value = state.value.copy(currencyId = event.id)
            is ProfileEvent.TimeZoneChanged -> state.value = state.value.copy(timeZoneId = event.id)
            is ProfileEvent.SaveProfile -> saveProfile(event.token)
            is ProfileEvent.ResetProfileSaved -> state.value = state.value.copy(profileSaved = false)
            is ProfileEvent.OnRemoveHeadFromQueue -> removeHeadMessage()
        }
    }

    private fun loadProfile(token: String) {
        viewModelScope.launch {
            state.value = state.value.copy(isLoading = true, loadError = null)
            profileRepository.getProfile(token).onEach { ds ->
                when (ds) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        state.value = state.value.copy(isLoading = false, loadError = "Failed to load profile")
                        appendToMessageQueue(ds.uiComponent)
                    }
                    is DataState.Data -> {
                        val body = ds.data
                        state.value = if (body?.status == true && body.data != null) {
                            state.value.copy(isLoading = false, profile = body.data, loadError = null)
                        } else {
                            state.value.copy(isLoading = false, loadError = body?.message ?: "Something went wrong")
                        }
                    }
                    is DataState.Loading ->
                        state.value = state.value.copy(isLoading = ds.progressBarState == ProgressBarState.Loading)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun loadProfileForEdit() {
        val p = state.value.profile ?: return
        val tzId = p.timeZone?.timeZoneId?.takeIf { it > 0 }
        val curId = p.preferredCurrency?.currencyId?.takeIf { it > 0 }
        val lang = p.preferredLanguage?.languageCode.orEmpty()
        val s = state.value

        state.value = s.copy(
            firstName = p.firstName.orEmpty(),
            lastName = p.lastName.orEmpty(),
            emailId = p.emailId.orEmpty(),
            mobileNumber = p.mobileNumber.orEmpty(),
            languageCode = lang,
            currencyId = curId,
            timeZoneId = tzId,
            // TODO: replace with lookup APIs. For now each list holds at least the user's current value.
            languageOptions = s.languageOptions.withOption(
                lang, p.preferredLanguage?.displayName ?: p.preferredLanguage?.nativeName ?: lang
            ),
            currencyOptions = s.currencyOptions.withOption(
                curId?.toString().orEmpty(),
                p.preferredCurrency?.let { "${it.currencyCode.orEmpty()} (${it.symbol.orEmpty()})" }.orEmpty()
            ),
            timeZoneOptions = s.timeZoneOptions.withOption(
                tzId?.toString().orEmpty(), p.timeZone?.timeZoneName.orEmpty()
            ),
            showValidation = false, saveError = null, profileSaved = false,
        )
    }

    private fun List<PickerOption>.withOption(key: String, label: String): List<PickerOption> =
        if (key.isBlank() || any { it.key == key }) this else this + PickerOption(key, label)

    private fun saveProfile(token: String) {
        val s = state.value
        state.value = s.copy(showValidation = true)

        val emailOk = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$").matches(s.emailId.trim())
        val mobileOk = s.mobileNumber.length in 7..15
        val currencyId = s.currencyId
        val timeZoneId = s.timeZoneId

        if (s.firstName.isBlank() || s.lastName.isBlank() || !emailOk || !mobileOk) {
            appendToMessageQueue(UIComponent.ToastSimple("Please fix the highlighted fields"))
            return
        }
        if (s.languageCode.isBlank() || currencyId == null || timeZoneId == null) {
            appendToMessageQueue(UIComponent.ToastSimple("Please select language, currency and time zone"))
            return
        }

        val request = UpdateProfileRequestDto(
            firstName = s.firstName.trim(),
            lastName = s.lastName.trim(),
            emailId = s.emailId.trim(),
            mobileNumber = s.mobileNumber,
            preferredLanguage = s.languageCode,
            preferredCurrencyId = currencyId,
            timeZoneId = timeZoneId,
        )

        viewModelScope.launch {
            state.value = state.value.copy(isSaving = true, saveError = null)
            profileRepository.updateProfile(token, request).onEach { ds ->
                when (ds) {
                    is DataState.NetworkStatus -> {}
                    is DataState.Response -> {
                        state.value = state.value.copy(isSaving = false, saveError = "Failed to update profile")
                        appendToMessageQueue(ds.uiComponent)
                    }
                    is DataState.Data -> {
                        if (ds.data?.status == true) {
                            state.value = state.value.copy(isSaving = false, profileSaved = true)
                            appendToMessageQueue(UIComponent.ToastSimple(ds.data.message ?: "Profile updated successfully"))
                            loadProfile(token) // refresh the profile screen
                        } else {
                            state.value = state.value.copy(
                                isSaving = false, saveError = ds.data?.message ?: "Something went wrong"
                            )
                            appendToMessageQueue(UIComponent.ToastSimple(ds.data?.message ?: "Profile update failed"))
                        }
                    }
                    is DataState.Loading ->
                        state.value = state.value.copy(isSaving = ds.progressBarState == ProgressBarState.Loading)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun appendToMessageQueue(uiComponent: UIComponent) {
        if (uiComponent is UIComponent.None) return
        val queue = state.value.errorQueue
        queue.add(uiComponent)
        state.value = state.value.copy(errorQueue = Queue(mutableListOf()))
        state.value = state.value.copy(errorQueue = queue)
    }

    private fun removeHeadMessage() {
        try {
            val queue = state.value.errorQueue
            queue.remove()
            state.value = state.value.copy(errorQueue = Queue(mutableListOf()))
            state.value = state.value.copy(errorQueue = queue)
        } catch (e: Exception) {
            println("AppDebug: removeHeadMessage: Nothing to remove")
        }
    }
}