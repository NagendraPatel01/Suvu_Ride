package com.suviridedriver.model.registration

data class RegistrationResponse(
    val message:String,
    val drivingLicence:String,
    val vehiclesDetails:String,
    val personalDetails:String,
    val selfie : String,
    val payment : String,
)