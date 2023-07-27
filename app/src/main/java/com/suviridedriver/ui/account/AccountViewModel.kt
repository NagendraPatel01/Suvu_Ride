package com.suviridedriver.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suviride.model.success_response.SuccessResponse
import com.suviridedriver.model.driver_details.GetDriverDetailResponse
import com.suviridedriver.model.get_profile.GetProfileResponse
import com.suviridedriver.repository.AccountRepository
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel@Inject constructor(private val accountRepository: AccountRepository) : ViewModel() {

    // For get all rides
    val driverLogoutResponse: LiveData<NetworkResult<SuccessResponse>>
        get() = accountRepository.driverLogoutResponse

    fun driverLogout() {
        viewModelScope.launch {
            accountRepository.driverLogout()
        }
    }

    val getDriverDetailResponseLiveData: LiveData<NetworkResult<GetDriverDetailResponse>>
        get() = accountRepository.getDriverDetailResponseLiveData

    fun getDriverDetail() {
        viewModelScope.launch {
            accountRepository.getDriverDetail()
        }
    }
}