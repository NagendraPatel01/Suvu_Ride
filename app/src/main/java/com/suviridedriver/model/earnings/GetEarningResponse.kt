package com.suviridedriver.model.earnings

data class GetEarningResponse(
    val message: String?,
    val success: Boolean?,
    val totalEarning: Int?,
    val totalRidesCount: Int?,
    val todayEarning: Int?,
    val todayRidesCount: Int?,
    val thisMonthEarning: Int?,
    val thisMonthRideCount: Int?,
    val totalRides: List<TotalRide>
)