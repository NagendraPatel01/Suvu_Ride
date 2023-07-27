package com.suviridedriver.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suviridedriver.api.APIServices
import com.suviridedriver.model.rides.RidesResponse
import com.suviridedriver.utils.CommonFunction
import com.suviridedriver.utils.NetworkResult
import javax.inject.Inject


class RideRepository @Inject constructor(private val APIServices: APIServices) {

    // Drivers Response and LiveData
    private val _getRidesResponse = MutableLiveData<NetworkResult<RidesResponse>>()
    val getRidesResponse: LiveData<NetworkResult<RidesResponse>>
        get() = _getRidesResponse

    suspend fun getRides() {
        _getRidesResponse.postValue(NetworkResult.Loading())
        val response = APIServices.getRides()
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

}