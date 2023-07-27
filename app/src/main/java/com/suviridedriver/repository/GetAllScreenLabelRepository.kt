package com.suviridedriver.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suviridecustomer.model.screenLabel.ScreenLabelRequest
import com.suviridecustomer.model.screenLabel.ScreenLabelResponse
import com.suviridedriver.api.APIServices
import com.suviridedriver.utils.CommonFunction.getError
import com.suviridedriver.utils.NetworkResult
import okhttp3.ResponseBody
import org.json.JSONObject
import javax.inject.Inject

class GetAllScreenLabelRepository @Inject constructor(private val APIServices: APIServices) {

    private val _allScreenLabelResponseLiveData = MutableLiveData<NetworkResult<ScreenLabelResponse>>()
    val allScreenLabelResponseLiveData: LiveData<NetworkResult<ScreenLabelResponse>>
        get() = _allScreenLabelResponseLiveData

    suspend fun getAllScreenLabel( screenLabelRequest: ScreenLabelRequest) {
        _allScreenLabelResponseLiveData.postValue(NetworkResult.Loading())

        val response = APIServices.getAllScreensByLanguage(screenLabelRequest)
        if (response.isSuccessful && response.body() != null) {
            _allScreenLabelResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(getError(response)))
        } else {
            _allScreenLabelResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

}