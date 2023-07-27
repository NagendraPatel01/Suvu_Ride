package com.suviridedriver.model.rides

data class RidesResponse(
    val message: String,
    val rides: List<Ride>,
    val success: Boolean
)