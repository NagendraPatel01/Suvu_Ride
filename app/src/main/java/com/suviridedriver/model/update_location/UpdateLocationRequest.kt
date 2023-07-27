package com.suviridedriver.model.update_location

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UpdateLocationRequest : Serializable {
    @SerializedName("driverlatitude")
    @Expose
    var driverlatitude: Double? = null

    @SerializedName("driverlongitude")
    @Expose
    var driverlongitude: Double? = null

    @SerializedName("angle")
    @Expose
    var angle: Float? = null
}