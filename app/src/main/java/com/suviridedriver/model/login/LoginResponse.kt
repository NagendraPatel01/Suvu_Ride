package com.suviridedriver.model.login

data class LoginResponse(
    val message: String,
    val success: Boolean,
    val token: String
)