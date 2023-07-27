package com.suviridedriver.model.driver_details

data class DrivingLicence(
    val address: String,
    val documentSubmission: String,
    val fullName: String,
    val gender: String,
    val licenceNumber: String,
    val uploadImage: String,
    val verification: String
)