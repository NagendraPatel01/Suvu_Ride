package com.suviridedriver.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suviride.model.success_response.SuccessResponse
import com.suviridedriver.api.APIServices
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
import com.suviridedriver.utils.CommonFunction
import com.suviridedriver.utils.NetworkResult
import javax.inject.Inject

class MainRepository @Inject constructor(private val APIServices: APIServices) {


    // Drivers Response and LiveData
    private val _updateLocationResponse = MutableLiveData<NetworkResult<UpdateLocationResponse>>()
    val updateLocationResponse: LiveData<NetworkResult<UpdateLocationResponse>>
        get() = _updateLocationResponse

    suspend fun updateLocation(updateLocationRequest: UpdateLocationRequest) {
        _updateLocationResponse.postValue(NetworkResult.Loading())
        val response = APIServices.updateLocation(updateLocationRequest)
        if (response.code()==204 && response.body() != null) {
            _updateLocationResponse.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _updateLocationResponse.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _updateLocationResponse.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

    // for accept ride
    private val _acceptRideResponse = MutableLiveData<NetworkResult<AcceptRideResponse>>()
    val acceptRideResponse: LiveData<NetworkResult<AcceptRideResponse>>
        get() = _acceptRideResponse

    suspend fun acceptRide(rideRequest: RideRequest) {
        _acceptRideResponse.postValue(NetworkResult.Loading())

        val response = APIServices.acceptRide(rideRequest)
        if (response.isSuccessful && response.body() != null) {
            _acceptRideResponse.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
           _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _acceptRideResponse.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _acceptRideResponse.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


    // for decline ride
    private val _declineRideResponse = MutableLiveData<NetworkResult<RejectRideRespone>>()
    val declineRideResponse: LiveData<NetworkResult<RejectRideRespone>>
        get() = _declineRideResponse

    suspend fun declineRide(rideRequest: RideRequest) {
        _declineRideResponse.postValue(NetworkResult.Loading())
        val response = APIServices.declineRide(rideRequest)
        Log.v("DeclineRes", response.body().toString() )
        if (response.isSuccessful && response.body() != null) {
            _declineRideResponse.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
           _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _declineRideResponse.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _declineRideResponse.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


    // for start ride
    private val _startRideResponse = MutableLiveData<NetworkResult<StartRideResponse>>()
    val startRideResponse: LiveData<NetworkResult<StartRideResponse>>
        get() = _startRideResponse

    suspend fun startRide(rideRequest: RideRequest) {
        _startRideResponse.postValue(NetworkResult.Loading())
        val response = APIServices.startRide(rideRequest)
        if (response.isSuccessful && response.body() != null) {
            _startRideResponse.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
          _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _startRideResponse.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _startRideResponse.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

    // for end ride
    private val _completeRideResponse = MutableLiveData<NetworkResult<StartRideResponse>>()
    val completeRideResponse: LiveData<NetworkResult<StartRideResponse>>
        get() = _completeRideResponse

    suspend fun completeRide(rideRequest: RideRequest) {
        _completeRideResponse.postValue(NetworkResult.Loading())
        val response = APIServices.completeRide(rideRequest)
        if (response.isSuccessful && response.body() != null) {
            _completeRideResponse.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
        _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _completeRideResponse.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _completeRideResponse.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

    // for get on going ride
    private val _getOngoingRideResponse = MutableLiveData<NetworkResult<GetOngoingRideResponse>>()
    val getOngoingRideResponse: LiveData<NetworkResult<GetOngoingRideResponse>>
        get() = _getOngoingRideResponse

    suspend fun getOngoingRide() {
        _getOngoingRideResponse.postValue(NetworkResult.Loading())
        val response = APIServices.getOngoingRide()
        if (response.isSuccessful && response.body() != null) {
            _getOngoingRideResponse.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
      _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _getOngoingRideResponse.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _getOngoingRideResponse.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


    // for for get time
    private val _navigateToPickupPointResponse =
        MutableLiveData<NetworkResult<NavigateToPickupPointResponse>>()
    val navigateToPickupPointResponse: LiveData<NetworkResult<NavigateToPickupPointResponse>>
        get() = _navigateToPickupPointResponse

    suspend fun navigateToPickupPoint(distance: Int) {
        _navigateToPickupPointResponse.postValue(NetworkResult.Loading())
        val response = APIServices.navigateToPickupPoint(distance)
        if (response.isSuccessful && response.body() != null) {
            _navigateToPickupPointResponse.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
 _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _navigateToPickupPointResponse.postValue(
                NetworkResult.Error(
                    CommonFunction.getError(
                        response
                    )
                )
            )
        } else {
            _navigateToPickupPointResponse.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


    // for for get time
    private val _reachedToDestinationResponse =
        MutableLiveData<NetworkResult<NavigateToPickupPointResponse>>()
    val reachedToDestinationResponse: LiveData<NetworkResult<NavigateToPickupPointResponse>>
        get() = _reachedToDestinationResponse

    suspend fun reachedToDestination(distance: Int) {
        _reachedToDestinationResponse.postValue(NetworkResult.Loading())
        val response = APIServices.reachedToDestination(distance)
        if (response.isSuccessful && response.body() != null) {
            _reachedToDestinationResponse.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
_allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _reachedToDestinationResponse.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _reachedToDestinationResponse.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

    // Drivers Response and LiveData
    private val _getRidesResponse = MutableLiveData<NetworkResult<TotalRemainingRidesResponse>>()
    val getRidesResponse: LiveData<NetworkResult<TotalRemainingRidesResponse>>
        get() = _getRidesResponse

    suspend fun getTotalRemainingRides() {
        _getRidesResponse.postValue(NetworkResult.Loading())
        val response = APIServices.getremainingAndTotalRides()
        if (response.isSuccessful && response.body() != null) {
            _getRidesResponse.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
               _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _getRidesResponse.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _getRidesResponse.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


    // for update fcm token
    private val _updateFCMResponse = MutableLiveData<NetworkResult<SuccessResponse>>()
    val updateFCMResponse: LiveData<NetworkResult<SuccessResponse>>
        get() = _updateFCMResponse

    suspend fun updateFCM(updateFCMToken: UpdateFCMToken) {
        _updateFCMResponse.postValue(NetworkResult.Loading())
        val response = APIServices.updateFCM(updateFCMToken)
        if (response.isSuccessful && response.body() != null) {
            _updateFCMResponse.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
               _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _updateFCMResponse.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _updateFCMResponse.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

    private val _endRideResponse = MutableLiveData<NetworkResult<SuccessResponse>>()
    val endRideResponse: LiveData<NetworkResult<SuccessResponse>>
        get() = _endRideResponse

    suspend fun endRide(rideRequest: RideRequest) {
        _endRideResponse.postValue(NetworkResult.Loading())
        val response = APIServices.endRide(rideRequest)
        if (response.isSuccessful && response.body() != null) {
            _endRideResponse.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
  _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _endRideResponse.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _endRideResponse.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

    private val _checkRideStatusResponse = MutableLiveData<NetworkResult<SuccessResponse>>()
    val checkRideStatusResponse: LiveData<NetworkResult<SuccessResponse>>
        get() = _checkRideStatusResponse

    suspend fun checkRideStatus(rideId: String) {
        _checkRideStatusResponse.postValue(NetworkResult.Loading())
        val response = APIServices.checkRideStatus(rideId)
        if (response.isSuccessful && response.body() != null) {
            _checkRideStatusResponse.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
  _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _checkRideStatusResponse.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _checkRideStatusResponse.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }



    private val _getDriverDetailResponseLiveData = MutableLiveData<NetworkResult<GetDriverDetailResponse>>()
    val getDriverDetailResponseLiveData: LiveData<NetworkResult<GetDriverDetailResponse>>
        get() = _getDriverDetailResponseLiveData

    suspend fun getDriverDetail() {
        _getDriverDetailResponseLiveData.postValue(NetworkResult.Loading())

        val response = APIServices.getDriverDetail()
        if (response.isSuccessful && response.body() != null) {
            _getDriverDetailResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _loginResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _getDriverDetailResponseLiveData.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _getDriverDetailResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

}