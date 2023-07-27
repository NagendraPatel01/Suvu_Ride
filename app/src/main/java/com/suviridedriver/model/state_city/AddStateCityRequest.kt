package com.suviridedriver.model.state_city

data class AddStateCityRequest(
    val city: String,
    val state: String,
    val address: String
)