package com.suviridedriver.model.driving_licence_detail

data class Data(
    val Status: String,
    val driverId: String,
    val fullName: String,
    val gender: String,
    val language: String,
    val address: String,
    val mobileNumber: Long,
    val token: String
)