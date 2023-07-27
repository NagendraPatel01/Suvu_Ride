package com.suviridedriver.ui.vehicle_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suviridedriver.model.image_upload.ImageuploadRespons
import com.suviridedriver.model.vehicle_detail.VehicleDetailRequest
import com.suviridedriver.model.vehicle_detail.VehicleDetailResponse
import com.suviridedriver.model.vehicle_detail.VehiclesTypesResponse
import com.suviridedriver.repository.VehicleDetailRepository
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject


@HiltViewModel
class VehicleDetailViewModel @Inject constructor(private val vehicleDetailRepository: VehicleDetailRepository) :
    ViewModel() {


    val submitVehiclimage: LiveData<NetworkResult<ImageuploadRespons>>
        get() = vehicleDetailRepository.submitVehiclimage

    fun imageupload(image: MultipartBody.Part) {
        viewModelScope.launch {
            vehicleDetailRepository.imageupload(image)
        }
    }

    val submitVehicleDetailsLiveData: LiveData<NetworkResult<VehicleDetailResponse>>
        get() = vehicleDetailRepository.submitVehicleDetailsLiveData

    fun submitVehicleDetails(vehicleDetailRequest: VehicleDetailRequest) {
        viewModelScope.launch {
            vehicleDetailRepository.submitVehicleDetails(vehicleDetailRequest)
        }
    }

    val vehicleTypesLiveData: LiveData<NetworkResult<VehiclesTypesResponse>>
        get() = vehicleDetailRepository.vehicleTypesLiveData

    fun getVehicleTypes() {
        viewModelScope.launch {
            vehicleDetailRepository.getVehicleTypes()
        }
    }


    fun validateCredentials(vehicleDetailRequest: VehicleDetailRequest): Pair<Boolean, String> {
        var result = Pair(true, "")
        if (vehicleDetailRequest.vehicleType!!.equals("Select Vehicle Type")) {
            result = Pair(false, "Please select Vehicle Type")
        } else if (vehicleDetailRequest.vehicleModelNumber!! == ""||vehicleDetailRequest.vehicleModelNumber!!.length<5) {
            result = Pair(false, "Please provide the vehicle model number")
        } else if (vehicleDetailRequest.registrationID!! == ""||vehicleDetailRequest.registrationID!!.length<10) {
            result = Pair(false, "Please provide the Registration ID")
        } else if (vehicleDetailRequest.dateofRegistration!!.equals("")) {
            result = Pair(false, "Please select date of Registration")
        } else if (vehicleDetailRequest.registrationValidity!!.equals("")) {
            result = Pair(false, "Please select registration Validity")
        }

        return result
    }
}