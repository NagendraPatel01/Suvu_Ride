package com.suviridedriver.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suviridedriver.model.driver_status.DriverStatusRequest
import com.suviridedriver.model.driver_status.DriverStatusResponse
import com.suviridedriver.repository.HomeRepository
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) :
    ViewModel() {

    // For Driver current location update
    val driverStatusResponse: LiveData<NetworkResult<DriverStatusResponse>>
        get() = homeRepository.driverStatusResponse

    fun updateDriverStatus(driverStatusRequest: DriverStatusRequest) {
        viewModelScope.launch {
            homeRepository.updateDriverStatus(driverStatusRequest)
        }
    }
}

