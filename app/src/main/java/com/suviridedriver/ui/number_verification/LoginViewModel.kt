package com.suviridedriver.ui.number_verification

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suviridedriver.model.driver_details.GetDriverDetailResponse
import com.suviridedriver.model.login.LoginRequest
import com.suviridedriver.model.login.LoginResponse
import com.suviridedriver.repository.LoginRepository
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) : ViewModel() {

    val loginResponseLiveData: LiveData<NetworkResult<LoginResponse>>
        get() = loginRepository.loginResponseLiveData

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            loginRepository.login(loginRequest)
        }
    }
    
    //getDriverDetail
    //================================================================================================================


    val getDriverDetailResponseLiveData: LiveData<NetworkResult<GetDriverDetailResponse>>
        get() = loginRepository.getDriverDetailResponseLiveData

    fun getDriverDetail() {
        viewModelScope.launch {
            loginRepository.getDriverDetail()
        }
    }

    fun loginVerification(
        drivingStatus: String,
        vehiclesStatus: String,
        addressStatus: String,
        takeSelfieStatus: String,
        paymentStatus: String
    ): Pair<Boolean, String> {
        var result = Pair(true, "")
        if (drivingStatus.isEmpty() || drivingStatus.equals("pending")) {
            result = Pair(false, "Please submit your driving details.")
        } else if (vehiclesStatus.isEmpty() || vehiclesStatus.equals("pending")) {
            result = Pair(false, "Please submit your vehicle details.")
        } else if (addressStatus.isEmpty() || addressStatus.length > 5) {
            result = Pair(false, "Please submit your personal details.")
        }else if (takeSelfieStatus.isEmpty() || takeSelfieStatus.equals("pending")) {
            result = Pair(false, "Please add your selfie.")
        }else if (paymentStatus.isEmpty() || paymentStatus.equals("pending")) {
            result = Pair(false, "Please make payment.")
        }

        return result
    }

}