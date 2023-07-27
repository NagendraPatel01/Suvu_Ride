package com.suviridedriver.ui.home

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.swipebutton_library.SwipeButton
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.maps.android.SphericalUtil.computeHeading
import com.suviridedriver.model.driver_status.DriverStatusRequest
import com.suviridedriver.ui.add_recharge.AddRechargeActivity
import com.suviridedriver.ui.bottom_navigation.MainActivity
import com.suviridedriver.ui.bottom_navigation.MainActivity.Companion.isOnHome
import com.suviridedriver.ui.bottom_navigation.MainActivity.Companion.isWindowShow
import com.suviridedriver.ui.bottom_navigation.MainViewModel
import com.suviridedriver.ui.bottom_navigation.RideStepData
import com.suviridedriver.ui.home.function.getAddress
import com.suviridedriver.ui.home.function.updateCamera
import com.suviridedriver.utils.*
import com.suviridedriver.utils.Constants.TAG
import dagger.hilt.android.AndroidEntryPoint
import id.ss564.lib.slidingbutton.SlidingButton
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.*


@AndroidEntryPoint
class HomeFragment : BaseFragment()
//    OnMarkerDragListener
{
    private var isOnGoing = false
    private var isMapZoom = false
    private lateinit var gMap: GoogleMap
    private val geocoder: Geocoder? = null
    val homeViewModel by viewModels<HomeViewModel>()
    val mainViewModel by viewModels<MainViewModel>()
    var tvTime: TextView? = null
    var tvRideStatus: TextView? = null
    var llRideStatus: LinearLayout? = null
    var llFeatures: LinearLayout? = null
    var rlStatus: LinearLayout? = null
    var llRides: LinearLayout? = null
    var tvRemainingRides: TextView? = null
    var tvTotalRides: TextView? = null
    var sw2: SwitchCompat? = null
    var ivCurrentLocation: ImageView? = null
    var ivCall: ImageView? = null
    var ivEndRide: SwipeButton? = null
    var ivNavigator: ImageView? = null
    var tvStatus: TextView? = null
    val life_cycle = "Home_Fragment"
    var destAddress: String? = null
    var pickupPoint: LatLng? = null
    var userLocationMarker: Marker? = null
    var dropPoint: LatLng? = null
    var userLocationAccuracyCircle: Circle? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        showLog("onCreateView")
        // Initialize view
        val view: View =
            inflater.inflate(com.suviridedriver.R.layout.fragment_home, container, false)

        // Initialize map fragment
        val supportMapFragment =
            childFragmentManager.findFragmentById(com.suviridedriver.R.id.map) as SupportMapFragment?

        // Async map
        supportMapFragment!!.getMapAsync { googleMap ->
            // Initialize map
            gMap = googleMap
        }

        return view
    }

    private fun showLog(s: String) {
        Log.i(life_cycle, "Frag_Home - $s")
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLog("onViewCreated")
        tvTime = requireView().findViewById(com.suviridedriver.R.id.tvTime)
        tvRideStatus = requireView().findViewById(com.suviridedriver.R.id.tvRideStatus)
        llRideStatus = requireView().findViewById(com.suviridedriver.R.id.llRideStatus)
        llRides = requireView().findViewById(com.suviridedriver.R.id.llRides)
        tvTotalRides = requireView().findViewById(com.suviridedriver.R.id.tvTotalRides)
        tvRemainingRides = requireView().findViewById(com.suviridedriver.R.id.tvRemainingRides)
        ivCurrentLocation = requireView().findViewById(com.suviridedriver.R.id.ivCurrentLocation)
        ivCall = requireView().findViewById(com.suviridedriver.R.id.ivCall)
        ivEndRide = requireView().findViewById(com.suviridedriver.R.id.ivEndRide)
        ivNavigator = requireView().findViewById(com.suviridedriver.R.id.ivNavigator)
        tvStatus = requireView().findViewById(com.suviridedriver.R.id.tvStatus)
        sw2 = requireView().findViewById(com.suviridedriver.R.id.sStatus)
        rlStatus = requireView().findViewById(com.suviridedriver.R.id.rlStatus)
        llFeatures = requireView().findViewById(com.suviridedriver.R.id.llFeatures)

        ivNavigator!!.setOnClickListener {
            isMapZoom = false
            ivNavigator!!.visibility = View.GONE
        }

        ivCurrentLocation?.setOnClickListener {
            val activity = activity as MainActivity
            activity.setCurrentLocation(
                MainActivity.mCurrentLatitude,
                MainActivity.mCurrentLongitude
            )

            /*animateMarker(
                LatLng(MainActivity.mCurrentLatitude, MainActivity.mCurrentLongitude),
                LatLng(22.71708677016305, 75.87409706009623),
                false
            )
            Toast.makeText(context, "animate marker", Toast.LENGTH_SHORT).show()*/

        }

        onlineofflinestatus()
        updateDriverStatusObserver()
        getTotalRemainingRidesObserver()

        llRides?.setOnClickListener {
            val intent = Intent(requireContext(), AddRechargeActivity::class.java)
            startActivity(intent)
        }


        sw2?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                updateDriverStatus("online")
                tvStatus?.text = "Online"
            } else {
                updateDriverStatus("offline")
                tvStatus?.text = "offline"
            }

        }
    }

    override fun onResume() {
        super.onResume()
        showLog("onResume")
        getRemainingTotalRides()
        rlStatus?.visibility = View.VISIBLE
        llFeatures?.visibility = View.VISIBLE
        val status = AppSession.getValue(requireContext(), Constants.USER_STATUS)

        if ( sw2?.isChecked == true) {
           // sw2?.isChecked = true
            tvStatus?.text = "online"
        } else {
            tvStatus?.text = "offline"
        }
    }

    fun setTime(msg: String) {
        //  tvTime!!.text = msg
        llRides?.visibility = View.GONE
        llRideStatus?.visibility = View.VISIBLE
        tvRideStatus?.text = msg
    }


    // for set markers pickup and drop location
    fun createRouteRide(
        currentLocation: LatLng,
        rideData: RideStepData,
        apiKey: String,
        location: Location?
    ) {
        try {
            if (gMap != null && this::gMap.isInitialized) {
                pickupPoint = LatLng(currentLocation.latitude, currentLocation.longitude)
                destAddress = rideData.destinationLocation
                dropPoint = LatLng(
                    rideData.destinationLatitude!!.toDouble(),
                    rideData.destinationLongitude!!.toDouble()
                )

                // for creating route
                val urll = getDirectionURL(currentLocation, dropPoint!!, apiKey)
                GetDirection(urll, location).execute()
                isOnGoing = true
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }



    // set bitmap using vector xml for high resolution marker
    private fun bitmapFromVector(): BitmapDescriptor {
        val background = ContextCompat.getDrawable(
            requireContext(),
            com.suviridedriver.R.drawable.ic_marker_direction
        )
        background!!.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)

        val bitmap = Bitmap.createBitmap(
            background.intrinsicWidth,
            background.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        background.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    // for get direction url
    private fun getDirectionURL(origin: LatLng, dest: LatLng, secret: String): String {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=driving" +
                "&key=$secret"
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetDirection(val url: String, val location: Location?) :
        AsyncTask<Void, Void, List<List<LatLng>>>() {
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body!!.string()

            val result = ArrayList<List<LatLng>>()
            try {
                val respObj = Gson().fromJson(data, MapData::class.java)
                val path = ArrayList<LatLng>()
                for (i in 0 until respObj.routes[0].legs[0].steps.size) {
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {

            gMap.clear()
            val lineoption = PolylineOptions()
            for (i in result.indices) {
                lineoption.addAll(result[i])
                lineoption.width(16f)
                lineoption.color(Color.parseColor("#0588fa"))
                lineoption.geodesic(true)
            }
            try {
                gMap.addPolyline(lineoption)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            // delay 30 millisecond for getting old and new location
            Handler().postDelayed({
                //  Toast.makeText(context, "change position", Toast.LENGTH_SHORT).show()
                //  changePositionSmoothly(markerPosition!!, pickupPoint!!)
            }, 30)

            gMap.addMarker(
                MarkerOptions().position(dropPoint!!)
                    .icon(
                        bitmapDescriptorFromVector(
                            requireContext(),
                            com.suviridedriver.R.drawable.drop_pin
                        )
                    )
                    .title(destAddress)
            )

            // mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickupPoint!!, 16f))

            /* if (isMapZoom) {
                 mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickupPoint!!, 16f))
                 ivNavigator!!.visibility = View.VISIBLE
             }*/


            /* val markerPosition = mGoogleMap.addMarker(
                 MarkerOptions()
                     .position(pickupPoint!!)
                     .title(getAddress(pickupPoint!!.latitude, pickupPoint!!.longitude))
                     .icon(bitmapFromVector())
             )*/

            gMap.addMarker(
                MarkerOptions()
                    .position(pickupPoint!!)
                    .title(getAddress(pickupPoint!!.latitude, pickupPoint!!.longitude, context))
                    .icon(bitmapFromVector())
            )

            // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickupPoint!!, 16F))
            // setUserLocationMarker(location!!)
            updateCamera(location, gMap)
        }
    }

    // for decode poly line
    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }

    // convert svg, png to vector
    fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }



/*    fun updateCamera(location: Location?, map: GoogleMap) {
                val currentPlace = CameraPosition.Builder()
            .target(LatLng(location!!.latitude,location.longitude))
            .bearing(location.bearing + 75)
//            .tilt(65.5f)
//            .zoom(16F)
            .build()
        map.moveCamera(CameraUpdateFactory.newCameraPosition(currentPlace))
    }*/


    private fun setUserLocationMarker(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        if (userLocationMarker == null) {
            //Create a new marker
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.icon(bitmapFromVector())
//            markerOptions.rotation(location.bearing)
//            markerOptions.anchor(0.5.toFloat(), 0.5.toFloat())
            userLocationMarker = gMap.addMarker(markerOptions)
            //  mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
        } else {
            //use the previously created marker
            userLocationMarker!!.setPosition(latLng)
//            userLocationMarker!!.setRotation(location.bearing)
            // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
        }
        if (userLocationAccuracyCircle == null) {
            val circleOptions = CircleOptions()
            circleOptions.center(latLng)
            circleOptions.strokeWidth(4f)
            circleOptions.strokeColor(Color.argb(255, 255, 0, 0))
            circleOptions.fillColor(Color.argb(32, 255, 0, 0))
            circleOptions.radius(location.accuracy.toDouble())
            userLocationAccuracyCircle = gMap.addCircle(circleOptions)
        } else {
            userLocationAccuracyCircle!!.setCenter(latLng)
            userLocationAccuracyCircle!!.setRadius(location.accuracy.toDouble())
        }
    }



    //--------------------------------------Step Flow----------------------------------------

    // step 0
    // for add marker on current location
    fun setCurrentLocation(latitude: Double, longitude: Double) {
        try {
            if (gMap != null && this::gMap.isInitialized) {
                gMap.clear()
                val sPosition = LatLng(latitude, longitude)
                gMap.addMarker(
                    MarkerOptions()
                        .position(sPosition)
                        .snippet(getAddress(latitude, longitude, context))
                        .title("I am here")
                )
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sPosition, 16f))
               // Log.i("maxZoomLevel","maxZoomLevel - ${mGoogleMap.maxZoomLevel}")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    // fun isCall()

    //---------------------------------------API----------------------------------------------

    // step 0
    // for location update
    private fun updateDriverStatus(Status: String) {
        homeViewModel.updateDriverStatus(DriverStatusRequest(Status))
    }

    private fun getRemainingTotalRides() {
        mainViewModel.getDriverDetail()
        //mainViewModel.getTotalRemainingRides()


    }

    private fun updateDriverStatusObserver() {
        homeViewModel.driverStatusResponse.observe(requireActivity()) {
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.i("driverStatus", "Success" + it.data)
                        //AppSession.save(requireContext(), Constants.USER_STATUS, it.data.Status)
                    }
                }
                is NetworkResult.Error -> {
                    Log.i("driverStatus", "Error" + it.message)

                }
                is NetworkResult.Loading -> {
                    Log.i("driverStatus", "Loading")
                }
                else -> {}
            }
        }
    }

    private fun getTotalRemainingRidesObserver() {
        mainViewModel.getRidesResponse.observe(requireActivity()) {
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.d(TAG, "Success" + it.data)
                        tvRemainingRides?.text = it.data.rides.remainingRides.toString()
                        tvTotalRides?.text = it.data.rides.totalRides.toString()
                    }
                }
                is NetworkResult.Error -> {
                    Log.d(TAG, "Error" + it.message)

                }
                is NetworkResult.Loading -> {
                    Log.d(TAG, "Loading")
                }
                else -> {}
            }
        }
    }

    override fun onPause() {
        super.onPause()
        showLog("onPause")
        if (isWindowShow && isOnHome) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                rlStatus?.visibility = View.GONE
                llFeatures?.visibility = View.GONE
                val activity = activity as MainActivity
                activity.callPictureInPicture()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        showLog("onStop")
    }

    //This methos is used to move the marker of each car smoothly when there are any updates of their position
    fun animateMarker(startPosition: LatLng, toPosition: LatLng, hideMarker: Boolean) {
        val marker: Marker = gMap.addMarker(
            MarkerOptions()
                .position(startPosition)
                .title("title")
                .snippet("snippet")
                .icon(
                    bitmapDescriptorFromVector(
                        requireContext(),
                        com.suviridedriver.R.drawable.iv_car_texi
                    )
                )
        )!!
        val handler = Handler()
        val start: Long = SystemClock.uptimeMillis()
        val duration: Long = 3000
        val interpolator: Interpolator = LinearInterpolator()
        handler.post(object : Runnable {
            override fun run() {
                val elapsed: Long = SystemClock.uptimeMillis() - start
                val t: Float = interpolator.getInterpolation(elapsed.toFloat() / duration)
                val lng = t * toPosition.longitude + (1 - t) * startPosition.longitude
                val lat = t * toPosition.latitude + (1 - t) * startPosition.latitude
                marker.position = LatLng(lat, lng)
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16)
                } else {
                    if (hideMarker) {
                        marker.isVisible = false
                    } else {
                        marker.isVisible = true
                    }
                }
            }
        })
    }

    // animation marker change position for smoothly
    private fun changePositionSmoothly(marker: Marker, newLatLng: LatLng) {
        // setup your value animator
        val animation = ValueAnimator.ofFloat(0f, 100f)
        var previousStep = 0f
        val deltaLatitude = newLatLng.latitude - marker.position.latitude
        val deltaLongitude = newLatLng.longitude - marker.position.longitude
        animation.duration = 1500

        // animation can be start for every update location
        animation.addUpdateListener { updatedAnimation ->
            val deltaStep = updatedAnimation.animatedValue as Float - previousStep
            previousStep = updatedAnimation.animatedValue as Float
            marker.position = LatLng(
                marker.position.latitude + deltaLatitude * deltaStep * 1 / 100,
                marker.position.longitude + deltaStep * deltaLongitude * 1 / 100
            )
        }
        animation.start()

        // and rotate marker smoothly
        rotateMarker(
            marker,
            getAngle(
                LatLng(marker.position.latitude, marker.position.longitude),
                newLatLng
            ).toFloat()

        )

        // updateCameraBearing(mGoogleMap,getAngle(LatLng(marker.position.latitude, marker.position.longitude), newLatLng).toFloat())
    }

    // animation rotation marker
    private fun rotateMarker(marker: Marker, toRotation: Float) {
        val handler = Handler()
        val start = SystemClock.uptimeMillis()
        val startRotation = marker.rotation
        val duration: Long = 300

        handler.post(object : Runnable {
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - start
                val t = LinearInterpolator().getInterpolation(elapsed.toFloat() / duration)

                val rot = t * toRotation + (1 - t) * startRotation

                marker.rotation = if (-rot > 180) rot / 2 else rot
                if (t < 1.0) {
                    handler.postDelayed(this, 16)
                }
            }
        })
    }

    private fun getAngle(fromLatLng: LatLng, toLatLng: LatLng): Double {

        // default angle is 0.0
        var heading = 0.0

        // if marker different, update heading
        if (fromLatLng != toLatLng) {
            heading = computeHeading(fromLatLng, toLatLng)
        }
        return heading
    }

    private fun updateCameraBearing(googleMap: GoogleMap?, bearing: Float) {
        if (googleMap == null) return
        val camPos = CameraPosition.builder(googleMap.cameraPosition // current Camera
            )
            .bearing(bearing)
            .build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos))
    }

/*    override fun onMarkerDrag(p0: Marker) {
        TODO("Not yet implemented")
    }

    override fun onMarkerDragEnd(marker: Marker) {
        Log.d(TAG, "onMarkerDragEnd: ")
        val latLng: LatLng = marker.getPosition()
        try {
            val addresses: List<Address> =
                geocoder!!.getFromLocation(latLng.latitude, latLng.longitude, 1)!!
            if (addresses.size > 0) {
                val address = addresses[0]
                val streetAddress = address.getAddressLine(0)
                marker.setTitle(streetAddress)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onMarkerDragStart(p0: Marker) {
        TODO("Not yet implemented")
    }*/

    private fun onlineofflinestatus(){


        mainViewModel.getDriverDetailResponseLiveData.observe(requireActivity()) {
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.d(TAG, "Success" + it.data)

                        tvStatus?.text=it.data.Status
                        tvRemainingRides?.text = it.data.remainingRides.toString()
                        tvTotalRides?.text = it.data.totalRides.toString()

                        sw2?.isChecked = tvStatus?.text!!.equals("online")

                    }
                }
                is NetworkResult.Error -> {
                    Log.d(TAG, "Error" + it.message)

                }
                is NetworkResult.Loading -> {
                    Log.d(TAG, "Loading")
                }
                else -> {}
            }
        }
    }

}
