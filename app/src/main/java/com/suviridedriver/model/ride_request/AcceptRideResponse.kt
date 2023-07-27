package com.suviridedriver.model.ride_request

data class AcceptRideResponse(
    val destinationLocation: String,
    val fullName: String,
    val message: String,
    val ratting: Float,
    val pickupLocation: String,
    val registrationID: String,
    val selfie: String,
    val remainingRides:Int,
    val success: Boolean,
    val confirmOtp:Int
    )
