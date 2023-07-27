package com.suviridedriver.model.registration

import com.google.gson.annotations.SerializedName

data class StatusReport (
    val drivingLicence:String,
    val vehiclesDetails:String,
    val personalDetails:String,
    val selfie:String,
    val payment:String
)