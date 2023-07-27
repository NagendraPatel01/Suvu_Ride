package com.suviridedriver.model.update_profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import java.io.Serializable

class UpdateProfileRequest : Serializable {

    @SerializedName("mobileNumber")
    @Expose
    var mobileNumber: String? = null

    @SerializedName("fullName")
    @Expose
    var fullName: String? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("selfie")
    @Expose
    var selfie: String? = null
}