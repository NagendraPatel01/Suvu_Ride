package com.suviridedriver.model.rating

data class RatingRequest(
    val ratting: Int,
    val ratingMessage: String,
    val ratingTages: String,
    val rideId: String
)