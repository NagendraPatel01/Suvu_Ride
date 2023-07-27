package com.suviridedriver.ui.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suviridedriver.model.registration.DocumentSubmissionRequest
import com.suviridedriver.model.registration.RegistrationResponse
import com.suviridedriver.repository.GetRegistrationRepository
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel2 @Inject constructor(private val getRegistrationRepository: GetRegistrationRepository):
    ViewModel() {
    val getRegistrationResponseLiveData: LiveData<NetworkResult<RegistrationResponse>>
        get() = getRegistrationRepository.registrationResponseLiveData

    fun getRegistrationCheck() {
        viewModelScope.launch {
            getRegistrationRepository.getRegistrationCheck()
        }
    }
}