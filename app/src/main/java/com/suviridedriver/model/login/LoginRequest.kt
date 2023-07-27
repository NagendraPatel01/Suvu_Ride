package com.suviridedriver.model.login

data class LoginRequest(
    val language: String,
    val mobileNumber: String,
    val deviceToken: String
)