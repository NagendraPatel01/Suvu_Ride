package com.suviridedriver.ui.personal_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suviride.model.success_response.SuccessResponse
import com.suviridedriver.model.driver_details.GetDriverDetailResponse
import com.suviridedriver.repository.ProfileRepository
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val profileRepository: ProfileRepository) : ViewModel() {

    val deleteDriverResponse: LiveData<NetworkResult<SuccessResponse>>
        get() = profileRepository.deleteDriverResponse

    fun deleteDriver() {
        viewModelScope.launch {
            profileRepository.deleteDriver()
        }
    }


    val getDriverDetailResponseLiveData: LiveData<NetworkResult<GetDriverDetailResponse>>
        get() = profileRepository.getDriverDetailResponseLiveData

    fun getDriverDetail() {
        viewModelScope.launch {
            profileRepository.getDriverDetail()
        }
    }

}