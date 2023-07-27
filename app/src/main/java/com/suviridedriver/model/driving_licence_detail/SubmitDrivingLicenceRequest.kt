package com.suviridedriver.model.driving_licence_detail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import java.io.Serializable

class SubmitDrivingLicenceRequest : Serializable {
    @SerializedName("fullName")
    @Expose
    var fullName:String? = null

    /*@SerializedName("address")
    @Expose
    var address:String? = null*/

    @SerializedName("gender")
    @Expose
    var gender: String? = null

    @SerializedName("licenceNumber")
    @Expose
    var licenceNumber: String? = null

    @SerializedName("issuedDate")
    @Expose
    var issuedDate: String? = null

    @SerializedName("validitiy")
    @Expose
    var validitiy: String? = null

    @SerializedName("uploadImage")
    @Expose
    var uploadImage: String? = null
}