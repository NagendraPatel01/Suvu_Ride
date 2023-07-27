package com.suviridedriver.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suviridedriver.api.APIServices
import com.suviridedriver.model.image_upload.ImageuploadRespons
import com.suviridedriver.model.vehicle_detail.VehicleDetailRequest
import com.suviridedriver.model.vehicle_detail.VehicleDetailResponse
import com.suviridedriver.model.vehicle_detail.VehiclesTypesResponse
import com.suviridedriver.utils.CommonFunction
import com.suviridedriver.utils.NetworkResult
import okhttp3.MultipartBody
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class VehicleDetailRepository @Inject constructor(private val APIServices: APIServices) {

    private val _submitVehicleDetailsLiveData =
        MutableLiveData<NetworkResult<VehicleDetailResponse>>()
    val submitVehicleDetailsLiveData: LiveData<NetworkResult<VehicleDetailResponse>>
        get() = _submitVehicleDetailsLiveData

    suspend fun submitVehicleDetails(vehicleDetailRequest: VehicleDetailRequest) {
        _submitVehicleDetailsLiveData.postValue(NetworkResult.Loading())

        val response = APIServices.submitVehicleDetails(vehicleDetailRequest)

        if (response.isSuccessful && response.body() != null) {
            _submitVehicleDetailsLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
              _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _submitVehicleDetailsLiveData.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _submitVehicleDetailsLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


    private val _vehicleTypesLiveData = MutableLiveData<NetworkResult<VehiclesTypesResponse>>()
    val vehicleTypesLiveData: LiveData<NetworkResult<VehiclesTypesResponse>>
        get() = _vehicleTypesLiveData

    suspend fun getVehicleTypes() {
        _vehicleTypesLiveData.postValue(NetworkResult.Loading())

        val response = APIServices.getVehicleTypes()

        if (response.isSuccessful && response.body() != null) {
            _vehicleTypesLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
              _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _vehicleTypesLiveData.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _vehicleTypesLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
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

/*
submitDrivingLicenceRequest.mobileNumber!!,
submitDrivingLicenceRequest.fullName!!,
submitDrivingLicenceRequest.address!!,
submitDrivingLicenceRequest.gender!!,
submitDrivingLicenceRequest.licenceNumber!!,
submitDrivingLicenceRequest.issuedDate!!,
submitDrivingLicenceRequest.validitiy!!,
submitDrivingLicenceRequest.uploadImage!!*/
