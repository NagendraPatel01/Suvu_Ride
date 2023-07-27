package com.suviridedriver.ui.home.function

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import android.os.Handler
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.suviridedriver.utils.Constants.TAG
import com.suviridedriver.utils.MapData
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*

fun updateCamera(location: Location?, map: GoogleMap) {
    val currentPlace = CameraPosition.Builder()
        .target(LatLng(location!!.latitude, location.longitude))
        .bearing(location.bearing + 75)
//            .tilt(65.5f)
            .zoom(16F)
        .build()
    map.moveCamera(CameraUpdateFactory.newCameraPosition(currentPlace))
}


// get address using lat lng
fun getAddress(lat: Double, lng: Double, context: Context?): String {
    var strAdd = ""
    val geocoder = Geocoder(context!!, Locale.getDefault())
    try {
        val addresses = geocoder.getFromLocation(lat, lng, 1)
        if (addresses != null) {
            val returnedAddress = addresses[0]
            val strReturnedAddress = StringBuilder("")
            for (i in 0..returnedAddress.maxAddressLineIndex) {
                strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
            }
            strAdd = strReturnedAddress.toString()
        } else {
            Log.d(TAG, "No Address returned!")
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        Log.d(TAG, "Canont get Address!")
    }
    return strAdd
}

