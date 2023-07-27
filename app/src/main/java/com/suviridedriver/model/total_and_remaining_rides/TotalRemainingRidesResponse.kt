package com.suviridedriver.model.total_and_remaining_rides

data class TotalRemainingRidesResponse(
    val message: String,
    val rides: Rides,
    val success: Boolean,
    val successCode: Int
)