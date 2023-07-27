package com.suviridedriver.model.get_profile

data class Vehicle(
    val __v: Int,
    val _id: String,
    val baseFare: Int,
    val cancellationFee: Int,
    val createdAt: String,
    val description: String,
    val farePerMin: Int,
    val gatewayFee: Int,
    val hourFare: Int,
    val isDeleted: Boolean,
    val lateNightEndTime: String,
    val lateNightFare: Int,
    val lateNightStartTime: String,
    val name: String,
    val perKmCharge: Int,
    val relaxationTimeInMins: String,
    val seats: Int,
    val status: String,
    val updatedAt: String,
    val uploadVehicleImage: String,
    val waitingFeePerMin: Int
)