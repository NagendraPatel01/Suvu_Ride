package com.suviridedriver.model.earnings

data class TotalRide(
    val customerId: CustomerId,
    val destinationLocation: String,
    val distance: String,
    val fare: String,
    val paymentMethod: String,
    val pickupLocation: String
)