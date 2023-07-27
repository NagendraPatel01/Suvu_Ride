package com.suviridedriver.ui.make_payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suviridedriver.model.ride_packages.RidesPackagesResponse
import com.suviridedriver.repository.MakePaymentRepository
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MakePaymentViewModel @Inject constructor(private val makePaymentRepository: MakePaymentRepository) : ViewModel() {

    val ridesPackagesResponseResponseLiveData: LiveData<NetworkResult<RidesPackagesResponse>>
        get() = makePaymentRepository.ridesPackagesResponseResponseLiveData

    fun getRidesPackages(vehicleId:String) {
        viewModelScope.launch {
            makePaymentRepository.getRidesPackages(vehicleId)
        }
    }
}