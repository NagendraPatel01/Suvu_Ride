package com.suviridedriver.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suviridedriver.api.APIServices
import com.suviridedriver.model.earnings.GetEarningResponse
import com.suviridedriver.utils.CommonFunction
import com.suviridedriver.utils.Constants.TAG
import com.suviridedriver.utils.NetworkResult
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject


class EarningRepository @Inject constructor(private val APIServices: APIServices) {

    // Drivers Response and LiveData
    private val _getEarningResponse = MutableLiveData<NetworkResult<GetEarningResponse>>()
    val getRidesResponse: LiveData<NetworkResult<GetEarningResponse>>
        get() = _getEarningResponse

    suspend fun getEarning() {
        _getEarningResponse.postValue(NetworkResult.Loading())
        val response = APIServices.getEarning()
        if (response.isSuccessful && response.body() != null) {
            _getEarningResponse.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
               _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _getEarningResponse.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _getEarningResponse.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

}