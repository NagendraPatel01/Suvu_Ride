package com.suviridedriver.model.get_ongoing_ride

data class GetOngoingRideResponse(
    val Ongoing: Boolean,
    val message: String,
    val ride: Ride,
    val success: Boolean
)