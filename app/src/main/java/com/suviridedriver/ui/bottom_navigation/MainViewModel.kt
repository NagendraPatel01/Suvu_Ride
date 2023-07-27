package com.suviridedriver.ui.bottom_navigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suviride.model.success_response.SuccessResponse
import com.suviridedriver.model.driver_details.GetDriverDetailResponse
import com.suviridedriver.model.get_ongoing_ride.GetOngoingRideResponse
import com.suviridedriver.model.get_profile.GetProfileResponse
import com.suviridedriver.model.navigate_to_pickup_point.NavigateToPickupPointRequest
import com.suviridedriver.model.navigate_to_pickup_point.NavigateToPickupPointResponse
import com.suviridedriver.model.ride_request.AcceptRideResponse
import com.suviridedriver.model.ride_request.RejectRideRespone
import com.suviridedriver.model.ride_request.RideRequest
import com.suviridedriver.model.start_ride.StartRideResponse
import com.suviridedriver.model.total_and_remaining_rides.TotalRemainingRidesResponse
import com.suviridedriver.model.update_fcm.UpdateFCMToken
import com.suviridedriver.model.update_location.UpdateLocationRequest
import com.suviridedriver.model.update_location.UpdateLocationResponse
import com.suviridedriver.repository.MainRepository
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {

    // For Driver current location update
    val updateLocationResponse: LiveData<NetworkResult<UpdateLocationResponse>>
        get() = mainRepository.updateLocationResponse

    fun updateLocation(updateLocationRequest: UpdateLocationRequest) {
        viewModelScope.launch {
            mainRepository.updateLocation(updateLocationRequest)
        }
    }

    // For total and remaining rides
    val getRidesResponse: LiveData<NetworkResult<TotalRemainingRidesResponse>>
        get() = mainRepository.getRidesResponse

    fun getTotalRemainingRides() {
        viewModelScope.launch {
            mainRepository.getTotalRemainingRides()
        }
    }

    // fro accept ride
    val acceptRideResponse: LiveData<NetworkResult<AcceptRideResponse>>
        get() = mainRepository.acceptRideResponse

    fun acceptRide(rideRequest: RideRequest) {
        viewModelScope.launch {
            mainRepository.acceptRide(rideRequest)
        }
    }

    // decline ride
    val declineRideResponse: LiveData<NetworkResult<RejectRideRespone>>
        get() = mainRepository.declineRideResponse

    fun declineRide(rideRequest: RideRequest) {
        viewModelScope.launch {
            mainRepository.declineRide(rideRequest)
        }
    }


    // fro start ride
    val startRideResponse: LiveData<NetworkResult<StartRideResponse>>
        get() = mainRepository.startRideResponse

    fun startRide(rideRequest: RideRequest) {
        viewModelScope.launch {
            mainRepository.startRide(rideRequest)
        }
    }


    // fro end ride
    val completeRideResponse: LiveData<NetworkResult<StartRideResponse>>
        get() = mainRepository.completeRideResponse

    fun completeRide(rideRequest: RideRequest) {
        viewModelScope.launch {
            mainRepository.completeRide(rideRequest)
        }
    }


    // for get on going ride
    val getOngoingRideResponse: LiveData<NetworkResult<GetOngoingRideResponse>>
        get() = mainRepository.getOngoingRideResponse

    fun getOngoingRide() {
        viewModelScope.launch {
            mainRepository.getOngoingRide()
        }
    }

    // for get time for driver to pickup
    val navigateToPickupPointResponse: LiveData<NetworkResult<NavigateToPickupPointResponse>>
        get() = mainRepository.navigateToPickupPointResponse

    fun navigateToPickupPoint(distance: Int) {
        viewModelScope.launch {
            mainRepository.navigateToPickupPoint(distance)
        }
    }


    // for get time for pickup to drop
    val reachedToDestinationResponse: LiveData<NetworkResult<NavigateToPickupPointResponse>>
        get() = mainRepository.reachedToDestinationResponse

    fun reachedToDestination(distance: Int) {
        viewModelScope.launch {
            mainRepository.reachedToDestination(distance)
        }
    }

    // for update fcm token
    val updateFCMResponse: LiveData<NetworkResult<SuccessResponse>>
        get() = mainRepository.updateFCMResponse

    fun updateFCM(updateFCMToken: UpdateFCMToken) {
        viewModelScope.launch {
            mainRepository.updateFCM(updateFCMToken)
        }
    }

    // for end ride
    val endRideResponse: LiveData<NetworkResult<SuccessResponse>>
        get() = mainRepository.endRideResponse

    fun endRide(rideRequest: RideRequest) {
        viewModelScope.launch {
            mainRepository.endRide(rideRequest)
        }
    }

    // for checking Ride Status
    val checkRideStatusResponse: LiveData<NetworkResult<SuccessResponse>>
        get() = mainRepository.checkRideStatusResponse

    fun checkRideStatus(rideId: String) {
        viewModelScope.launch {
            mainRepository.checkRideStatus(rideId)
        }
    }

    val getDriverDetailResponseLiveData: LiveData<NetworkResult<GetDriverDetailResponse>>
        get() = mainRepository.getDriverDetailResponseLiveData

    fun getDriverDetail() {
        viewModelScope.launch {
            mainRepository.getDriverDetail()
        }
    }

}