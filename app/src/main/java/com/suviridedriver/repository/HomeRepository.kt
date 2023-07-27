package com.suviridedriver.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suviridedriver.api.APIServices
import com.suviridedriver.model.driver_status.DriverStatusRequest
import com.suviridedriver.model.driver_status.DriverStatusResponse
import com.suviridedriver.utils.CommonFunction
import com.suviridedriver.utils.NetworkResult
import org.json.JSONObject
import javax.inject.Inject


class HomeRepository @Inject constructor(private val APIServices: APIServices) {

    // for for get time
    private val _driverStatusResponse = MutableLiveData<NetworkResult<DriverStatusResponse>>()
    val driverStatusResponse: LiveData<NetworkResult<DriverStatusResponse>>
        get() = _driverStatusResponse

    suspend fun updateDriverStatus(driverStatusRequest: DriverStatusRequest) {
        _driverStatusResponse.postValue(NetworkResult.Loading())
        val response = APIServices.updateDriverStatus(driverStatusRequest)
        if (response.code()==204 && response.body() != null) {
            _driverStatusResponse.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
               _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _driverStatusResponse.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _driverStatusResponse.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


}