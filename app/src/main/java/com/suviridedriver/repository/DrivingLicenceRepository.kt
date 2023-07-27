package com.suviridedriver.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suviridedriver.api.APIServices
import com.suviridedriver.model.driving_licence_detail.SubmitDrivingLicenceRequest
import com.suviridedriver.model.driving_licence_detail.SubmitDrivingLicenceResponse
import com.suviridedriver.model.image_upload.ImageuploadRespons
import com.suviridedriver.utils.CommonFunction
import com.suviridedriver.utils.NetworkResult
import okhttp3.MultipartBody
import javax.inject.Inject

class DrivingLicenceRepository @Inject constructor(private val APIServices: APIServices) {

    private val _submitDrivingLicenceLiveData = MutableLiveData<NetworkResult<SubmitDrivingLicenceResponse>>()
    val submitDrivingLicenceLiveData: LiveData<NetworkResult<SubmitDrivingLicenceResponse>>
        get() = _submitDrivingLicenceLiveData

    suspend fun submitDrivingLicenceDetails(submitDrivingLicenceRequest: SubmitDrivingLicenceRequest) {
        _submitDrivingLicenceLiveData.postValue(NetworkResult.Loading())
        val response = APIServices.submitDrivingLicenceDetails(submitDrivingLicenceRequest)
        if (response.isSuccessful && response.body() != null) {
            _submitDrivingLicenceLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
               _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _submitDrivingLicenceLiveData.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _submitDrivingLicenceLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
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