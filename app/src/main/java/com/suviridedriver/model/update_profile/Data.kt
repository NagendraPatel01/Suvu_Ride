package com.suviridedriver.model.update_profile

data class Data(
    val Status: String,
    val address: String,
    val driverId: String,
    val fullName: String,
    val gender: String,
    val language: String,
    val mobileNumber: Long,
    val selfie: String,
    val token: String
)