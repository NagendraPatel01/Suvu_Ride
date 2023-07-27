package com.suviridedriver.ui.rides

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suviridedriver.model.rides.RidesResponse
import com.suviridedriver.repository.RideRepository
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RideViewModel@Inject constructor(private val rideRepository: RideRepository) : ViewModel() {

    // For get all rides
    val getRidesResponse: LiveData<NetworkResult<RidesResponse>>
        get() = rideRepository.getRidesResponse

    fun getRides() {
        viewModelScope.launch {
            rideRepository.getRides()
        }
    }
}