package com.suviridedriver.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suviridedriver.api.APIServices
import com.suviridedriver.model.ride_packages.RidesPackagesResponse
import com.suviridedriver.utils.CommonFunction
import com.suviridedriver.utils.NetworkResult
import javax.inject.Inject

class MakePaymentRepository @Inject constructor(private val APIServices: APIServices) {

    private val _ridesPackagesResponseResponseLiveData = MutableLiveData<NetworkResult<RidesPackagesResponse>>()
    val ridesPackagesResponseResponseLiveData: LiveData<NetworkResult<RidesPackagesResponse>>
        get() = _ridesPackagesResponseResponseLiveData

    suspend fun getRidesPackages(vehicleId:String) {
        val response = APIServices.getRidesPackages(vehicleId)
        if (response.isSuccessful && response.body() != null) {
            _ridesPackagesResponseResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
_allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _ridesPackagesResponseResponseLiveData.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _ridesPackagesResponseResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
}