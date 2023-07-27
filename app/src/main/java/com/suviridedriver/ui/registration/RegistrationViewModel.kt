package com.suviridedriver.ui.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suviridedriver.model.verification.DocumentsVerificationResponse
import com.suviridedriver.repository.RegistrationRepository
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(private val registrationRepository: RegistrationRepository) :
    ViewModel() {

    val documentsVerificationResponseLiveData: LiveData<NetworkResult<DocumentsVerificationResponse>>
        get() = registrationRepository.documentsVerificationResponseLiveData

    fun documentsVerification() {
        viewModelScope.launch {
            registrationRepository.documentsVerification()
        }
    }
}