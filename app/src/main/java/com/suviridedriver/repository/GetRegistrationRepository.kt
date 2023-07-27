package com.suviridedriver.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suviridedriver.api.APIServices
import com.suviridedriver.model.registration.DocumentSubmissionRequest
import com.suviridedriver.model.registration.RegistrationResponse
import com.suviridedriver.utils.CommonFunction
import com.suviridedriver.utils.NetworkResult
import javax.inject.Inject

class GetRegistrationRepository @Inject constructor(private val APIServices : APIServices) {

    private val _registrationResponseLiveData = MutableLiveData<NetworkResult<RegistrationResponse>>()
    val registrationResponseLiveData: LiveData<NetworkResult<RegistrationResponse>>
        get() = _registrationResponseLiveData
    suspend fun getRegistrationCheck() {

        _registrationResponseLiveData.postValue(NetworkResult.Loading())
        val response = APIServices.statusChecker()
        if(response.isSuccessful && response.body() != null){
            _registrationResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        }else if (response.errorBody() != null){
            _registrationResponseLiveData.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        }else{
            _registrationResponseLiveData.postValue(NetworkResult.Error("Something went Wrong"))
        }
    }
}