package com.suviridedriver.model.vehicle_detail

data class VehiclesTypesResponse(
    val result: List<AllVehical>,
    val message: String,
    val success: Boolean
)