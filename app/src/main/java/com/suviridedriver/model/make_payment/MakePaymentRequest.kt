package com.suviridedriver.model.make_payment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import java.io.Serializable
import java.util.Date

/*paymentMode : UPI
orderId : 644a466017e7e4854d967806
status : OK
txTime : 2023-04-27 20:39:34
referenceId : 1491412362
txMsg : Transaction Successful
signature : yEiq6v2mDs3x9L4hghlXCcHom5LQSr8yLTB4mCUh43E=
orderAmount : 500.00
txStatus : SUCCESS*/


/*{
    "paymentMode":"UPI",
    "orderId":"643d29af7780194c57f39c07",
    "status":"OK",
    "txTime":"2023-04-27 20:39:34",
    "referenceId":"1491412362",
    "txMsg":"Transaction Successful",
    "signature":"yEiq6v2mDs3x9L4hghlXCcHom5LQSr8yLTB4mCUh43E=",
    "orderAmount":"500.00",
    "txStatus":"SUCCESS"
}*/

class MakePaymentRequest : Serializable {

    @SerializedName("paymentMode")
    @Expose
    var paymentMode: String?=null

    @SerializedName("packageId")
    @Expose
    var packageId: String?=null

    @SerializedName("orderId")
    @Expose
    var orderId: String?=null

    @SerializedName("status")
    @Expose
    var status: String?=null

    @SerializedName("txTime")
    @Expose
    var txTime: String?=null

    @SerializedName("referenceId")
    @Expose
    var referenceId: String?=null

    @SerializedName("txMsg")
    @Expose
    var txMsg: String?=null

    @SerializedName("signature")
    @Expose
    var signature: String?=null

    @SerializedName("orderAmount")
    @Expose
    var orderAmount: String?=null

    @SerializedName("txStatus")
    @Expose
    var txStatus: String?=null
}