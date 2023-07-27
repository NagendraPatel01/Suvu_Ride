package com.suviridecustomer.model.screenLabel

data class ScreenLabelResponse(
    val data: List<Data>,
    val message: String,
    val success: Boolean,
    val successCode: Int
)