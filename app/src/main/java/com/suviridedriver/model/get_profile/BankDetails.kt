package com.suviridedriver.model.get_profile

data class BankDetails(
    val IFSC: String,
    val accountNumber: Int,
    val verification: String
)