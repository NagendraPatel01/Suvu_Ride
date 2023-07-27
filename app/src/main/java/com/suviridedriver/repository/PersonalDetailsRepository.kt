package com.suviridedriver.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suviride.model.success_response.SuccessResponse
import com.suviridedriver.api.APIServices
import com.suviridedriver.model.state_city.AddStateCityRequest
import com.suviridedriver.model.state_city.CityResponse
import com.suviridedriver.model.state_city.StateResponse
import com.suviridedriver.utils.CommonFunction
import com.suviridedriver.utils.NetworkResult
import javax.inject.Inject

class PersonalDetailsRepository @Inject constructor(private val APIServices: APIServices) {

    private val _stateResponseResponse = MutableLiveData<NetworkResult<StateResponse>>()
    val stateResponseResponse: LiveData<NetworkResult<StateResponse>>
        get() = _stateResponseResponse

    suspend fun getState() {
        _stateResponseResponse.postValue(NetworkResult.Loading())

        val response = APIServices.getState()
        if (response.isSuccessful && response.body() != null) {
            _stateResponseResponse.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
  _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _stateResponseResponse.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _stateResponseResponse.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


    private val _cityResponse = MutableLiveData<NetworkResult<CityResponse>>()
    val cityResponse: LiveData<NetworkResult<CityResponse>>
        get() = _cityResponse

    suspend fun getCities(stateId : String) {
        _cityResponse.postValue(NetworkResult.Loading())
        val response = APIServices.getCities(stateId)
        if (response.isSuccessful && response.body() != null) {
            _cityResponse.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
  _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _cityResponse.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _cityResponse.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


    private val _addStateCityResponse = MutableLiveData<NetworkResult<SuccessResponse>>()
    val addStateCityResponse: LiveData<NetworkResult<SuccessResponse>>
        get() = _addStateCityResponse

    suspend fun updateStateCity(addStateCityRequest: AddStateCityRequest) {
        _addStateCityResponse.postValue(NetworkResult.Loading())
        val response = APIServices.updateStateCity(addStateCityRequest)
        if (response.isSuccessful && response.body() != null) {
            _addStateCityResponse.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
  _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _addStateCityResponse.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _addStateCityResponse.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

}