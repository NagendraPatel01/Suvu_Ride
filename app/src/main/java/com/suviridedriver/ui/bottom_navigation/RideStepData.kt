package com.suviridedriver.ui.bottom_navigation

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RideStepData: Serializable {

    @SerializedName("customer_name")
    @Expose
    var customer_name: String? = null

    @SerializedName("customer_rating")
    @Expose
    var customer_rating: String? = null

    @SerializedName("customer_id")
    @Expose
    var customer_id: String? = null

    @SerializedName("customer_number")
    @Expose
    var customer_number: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("pickupLocation")
    @Expose
    var pickupLocation: String? = null

    @SerializedName("pickupLongitude")
    @Expose
    var pickupLongitude: String? = null

    @SerializedName("fare")
    @Expose
    var fare: String? = null

    @SerializedName("discount")
    @Expose
    var discount: String? = null

    @SerializedName("finalFare")
    @Expose
    var finalFare: String? = null

    @SerializedName("pickupLatitude")
    @Expose
    var pickupLatitude: String? = null

    @SerializedName("destinationLocation")
    @Expose
    var destinationLocation: String? = null

    @SerializedName("ride_id")
    @Expose
    var ride_id: String? = null

    @SerializedName("numberOfPassengers")
    @Expose
    var numberOfPassengers: String? = null

    @SerializedName("destinationLongitude")
    @Expose
    var destinationLongitude: String? = null

    @SerializedName("destinationLatitude")
    @Expose
    var destinationLatitude: String? = null

    @SerializedName("confirmOtp")
    @Expose
    var confirmOtp: Int? = null

    @SerializedName("selfie")
    @Expose
    var selfie: String? = null
}