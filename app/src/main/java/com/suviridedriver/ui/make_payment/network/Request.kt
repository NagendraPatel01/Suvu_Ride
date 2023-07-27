package com.suviridedriver.ui.make_payment.network;

data class Request(
    val orderAmount: Int,
    val orderCurrency: String,
    val orderId: String
)