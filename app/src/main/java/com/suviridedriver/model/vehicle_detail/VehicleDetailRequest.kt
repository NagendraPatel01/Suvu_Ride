package com.suviridedriver.model.vehicle_detail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import java.io.Serializable
import java.util.Date

class VehicleDetailRequest : Serializable {

    @SerializedName("vehicleType")
    @Expose
    var vehicleType: String?=null

    @SerializedName("vehicleModelNumber")
    @Expose
    var vehicleModelNumber: String?=null

    @SerializedName("registrationID")
    @Expose
    var registrationID:String? = null

    @SerializedName("dateofRegistration")
    @Expose
    var dateofRegistration:String? = null

    @SerializedName("registrationValidity")
    @Expose
    var registrationValidity: String? = null

    @SerializedName("imageOfRegistrationCard")
    @Expose
    var imageOfRegistrationCard: String? = null
}