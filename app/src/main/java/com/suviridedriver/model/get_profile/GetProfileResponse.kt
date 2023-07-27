package com.suviridedriver.model.get_profile

data class GetProfileResponse(
    val `data`: Data,
    val success: Boolean,
    val vehicle: List<Vehicle>
)