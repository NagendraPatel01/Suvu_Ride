package com.suviridedriver.model.get_ongoing_ride

data class Ride(
    val confirmOtp: Int,
    val customerId: CustomerId,
    val customer_number: String,
    val destinationLatitude: Double,
    val destinationLocation: String,
    val destinationLongitude: Double,
    val distance: Double,
    val driverId: String,
    val fare: Int,
    val discount: Int,
    val finalFare: Int,
    val numberOfPassengers: Int,
    val pickupLatitude: Double,
    val pickupLocation: String,
    val pickupLongitude: Double,
    val rideId: String,
    val status: String,
    val customer_rating: String,
    val crnnumber: String,
    val vehicleType: String
)