package com.suviridedriver.model.ride_request

data class RejectRideRespone(
    val message: String,
    val remainingRides: Int,
    val success: Boolean
)