package com.suviridedriver.model.rides

data class Data(
    val customerId: String,
    val customerImage: String,
    val customerName: String,
    val destinationLocation: String,
    val distance: Double,
    val fare: Int,
    val paymentMethod: String,
    val pickupLocation: String
)