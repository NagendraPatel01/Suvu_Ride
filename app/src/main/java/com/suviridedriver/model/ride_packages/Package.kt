package com.suviridedriver.model.ride_packages

data class Package(
    val _id: String,
    val description: String,
    val price: Int,
    val totalRides: Int,
    val validity: String,
    val name: String,
    val order_id: String,
    val currency: String
)