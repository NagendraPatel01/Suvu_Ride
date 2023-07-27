package com.suviridedriver.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suviridedriver.api.APIServices
import com.suviridedriver.model.image_upload.ImageuploadRespons
import com.suviridedriver.model.update_profile.UpdateProfileRequest
import com.suviridedriver.model.update_profile.UpdateProfileResponse
import com.suviridedriver.utils.CommonFunction
import com.suviridedriver.utils.NetworkResult
import okhttp3.MultipartBody
import javax.inject.Inject

class UpdateProfileRepository @Inject constructor(private val APIServices: APIServices) {

    private val _updateProfileResponse = MutableLiveData<NetworkResult<UpdateProfileResponse>>()
    val updateProfileResponse: LiveData<NetworkResult<UpdateProfileResponse>>
        get() = _updateProfileResponse

    suspend fun updateUserProfile(updateProfileRequest: UpdateProfileRequest) {
        _updateProfileResponse.postValue(NetworkResult.Loading())

        val response = APIServices.updateUserProfile(updateProfileRequest)

        if (response.isSuccessful && response.body() != null) {
            _updateProfileResponse.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
             _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _updateProfileResponse.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _updateProfileResponse.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

    private val _submitVehiclimage =
        MutableLiveData<NetworkResult<ImageuploadRespons>>()
    val submitVehiclimage: LiveData<NetworkResult<ImageuploadRespons>>
        get() = _submitVehiclimage

    suspend fun imageupload(image: MultipartBody.Part) {
        _submitVehiclimage.postValue(NetworkResult.Loading())

        val response = APIServices.imageupload(image)

        if (response.isSuccessful && response.body() != null) {
            _submitVehiclimage.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
              _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _submitVehiclimage.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _submitVehiclimage.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
}