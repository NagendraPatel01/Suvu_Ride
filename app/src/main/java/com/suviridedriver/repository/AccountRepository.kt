package com.suviridedriver.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suviride.model.success_response.SuccessResponse
import com.suviridedriver.api.APIServices
import com.suviridedriver.model.driver_details.GetDriverDetailResponse
import com.suviridedriver.model.get_profile.GetProfileResponse
import com.suviridedriver.utils.CommonFunction
import com.suviridedriver.utils.CommonFunction.getError
import com.suviridedriver.utils.NetworkResult
import javax.inject.Inject


class AccountRepository @Inject constructor(private val APIServices: APIServices) {

    // Drivers Response and LiveData
    private val _driverLogoutResponse = MutableLiveData<NetworkResult<SuccessResponse>>()
    val driverLogoutResponse: LiveData<NetworkResult<SuccessResponse>>
        get() = _driverLogoutResponse

    suspend fun driverLogout() {
        _driverLogoutResponse.postValue(NetworkResult.Loading())
        val response = APIServices.driverLogout()
        if (response.isSuccessful && response.body() != null) {
            _driverLogoutResponse.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
             _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _driverLogoutResponse.postValue(NetworkResult.Error(getError(response)))
        } else {
            _driverLogoutResponse.postValue(NetworkResult.Error("Something Went Wrong"))
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