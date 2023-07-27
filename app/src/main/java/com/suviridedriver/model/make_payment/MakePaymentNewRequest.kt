package com.suviridedriver.model.make_payment

data class MakePaymentNewRequest(
    val orderAmount: String,
    val orderId: String,
    val paymentMode: String,
    val referenceId: String,
    val signature: String,
    val status: String,
    val txMsg: String,
    val txStatus: String,
    val txTime: String
)