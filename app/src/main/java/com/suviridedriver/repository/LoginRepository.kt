package com.suviridedriver.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suviridedriver.api.APIServices
import com.suviridedriver.model.driver_details.GetDriverDetailResponse
import com.suviridedriver.model.login.LoginRequest
import com.suviridedriver.model.login.LoginResponse
import com.suviridedriver.utils.CommonFunction
import com.suviridedriver.utils.NetworkResult
import org.json.JSONObject
import javax.inject.Inject

class LoginRepository @Inject constructor(private val APIServices: APIServices) {

    private val _loginResponseLiveData = MutableLiveData<NetworkResult<LoginResponse>>()
    val loginResponseLiveData: LiveData<NetworkResult<LoginResponse>>
        get() = _loginResponseLiveData

    suspend fun login(loginRequest: LoginRequest) {
        _loginResponseLiveData.postValue(NetworkResult.Loading())

        val response = APIServices.login(loginRequest)
        if (response.isSuccessful && response.body() != null) {
            _loginResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _loginResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _loginResponseLiveData.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _loginResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


    //getDriverDetail
    //'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''

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