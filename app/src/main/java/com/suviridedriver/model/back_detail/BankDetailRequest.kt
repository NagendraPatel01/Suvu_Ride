package com.suviridedriver.model.back_detail

data class BankDetailRequest(
    val IFSC: String,
    val accountNumber: String
)