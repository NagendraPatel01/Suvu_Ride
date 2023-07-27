package com.suviridedriver.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suviride.model.success_response.SuccessResponse
import com.suviridedriver.api.APIServices
import com.suviridedriver.model.rating.RatingRequest
import com.suviridedriver.utils.CommonFunction
import com.suviridedriver.utils.NetworkResult
import javax.inject.Inject

class RatingRepository @Inject constructor(private val APIServices: APIServices) {

    private val _submitRatingResponseLiveData = MutableLiveData<NetworkResult<SuccessResponse>>()
    val submitRatingResponseLiveData: LiveData<NetworkResult<SuccessResponse>>
        get() = _submitRatingResponseLiveData

    suspend fun submitRating(ratingRequest: RatingRequest) {
        _submitRatingResponseLiveData.postValue(NetworkResult.Loading())

        val response = APIServices.submitRating(ratingRequest)
        if (response.isSuccessful && response.body() != null) {
            _submitRatingResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
_allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _submitRatingResponseLiveData.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _submitRatingResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
}