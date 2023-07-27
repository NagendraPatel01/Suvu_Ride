package com.suviridecustomer.model.delete_customer

data class DeleteCustomerResponse(
    val message: String,
    val nextScreen: String,
    val success: Boolean,
    val successCode: Int
)