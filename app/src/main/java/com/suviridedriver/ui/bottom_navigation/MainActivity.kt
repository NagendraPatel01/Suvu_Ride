package com.suviridedriver.ui.bottom_navigation

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.PictureInPictureParams
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Rational
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.androidisland.ezpermission.EzPermission
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.suviridecustomer.ui.number_verification.GenericKeyEventOTP
import com.suviridecustomer.ui.number_verification.GenericTextWatcherOTP
import com.suviridedriver.R
import com.suviridedriver.databinding.ActivityMainBinding
import com.suviridedriver.model.navigate_to_pickup_point.NavigateToPickupPointRequest
import com.suviridedriver.model.ride_request.RideRequest
import com.suviridedriver.model.update_fcm.UpdateFCMToken
import com.suviridedriver.model.update_location.UpdateLocationRequest
import com.suviridedriver.services.location.LocationUtil
import com.suviridedriver.services.location.livedata.LocationViewModel
import com.suviridedriver.ui.account.AccountFragment
import com.suviridedriver.ui.earning.EarningFragment
import com.suviridedriver.ui.home.HomeFragment
import com.suviridedriver.ui.ride_payment.RidePaymentActivity
import com.suviridedriver.ui.rides.RideFragment
import com.suviridedriver.utils.*
import com.suviridedriver.utils.Constants.TAG
import dagger.hilt.android.AndroidEntryPoint
import id.ss564.lib.slidingbutton.SlidingButton

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private var location: Location? = null
    private var navigateStep = ""
    private lateinit var binding: ActivityMainBinding
    private lateinit var locationViewModel: LocationViewModel
    val mainViewModel by viewModels<MainViewModel>()
    private var isGPSEnabled = false
    val context: Context = this

    val LOCATION_PERMISSION_REQUEST = 101
    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val homeFragment = HomeFragment()
    val earningFragment = EarningFragment()
    val rideFragment = RideFragment()
    val accountFragment = AccountFragment()

    val rideStepData = RideStepData()
    var isOTPScreenVisible = false
    var showStartRideScreen = false
    var showEndRideScreen = false
    var newRideNotification = false

    var pressedTime = 0L

    companion object {
        var mCurrentLatitude = 0.0
        var mCurrentLongitude = 0.0
        var mPlayer: MediaPlayer? = null
        var isWindowShow = false
        var isOnHome = true
    }

    private var editText1: EditText? = null
    private var editText2: EditText? = null
    private var editText3: EditText? = null
    private var editText4: EditText? = null

    private var acceptRejectDialog: Dialog? = null
    private var navigateToPickupDialog: Dialog? = null
    private var navigateToDropPointDialog: Dialog? = null
    private var enterOTPDialog: Dialog? = null
    private var startRideDialog: Dialog? = null
    private var endRideDialog: Dialog? = null
    private var endOngoing: Dialog? = null

    // step 1
    // broadcast receiver
    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                if (intent != null) {
                    rideRequest(intent)
                }
            } catch (e: Exception) {
                showToast(e.message)
                showLog("receiver_error", e.message)
            }

            //   rideRequest(intent!!)
        }
    }

    fun rideRequest(intent: Intent) {
        rideStepData.customer_name = intent.getStringExtra("customer_name")
        rideStepData.customer_rating = intent.getStringExtra("customer_rating")
        rideStepData.customer_id = intent.getStringExtra("customer_id")
        rideStepData.customer_number = intent.getStringExtra("customer_mobileNumber")
        rideStepData.status = intent.getStringExtra("status")
        rideStepData.pickupLocation = intent.getStringExtra("pickupLocation")
        rideStepData.pickupLongitude = intent.getStringExtra("pickupLongitude")
        rideStepData.pickupLatitude = intent.getStringExtra("pickupLatitude")
        rideStepData.destinationLocation = intent.getStringExtra("destinationLocation")
        rideStepData.ride_id = intent.getStringExtra("ride_id")
        rideStepData.numberOfPassengers = intent.getStringExtra("numberOfPassengers")
        rideStepData.destinationLongitude = intent.getStringExtra("destinationLongitude")
        rideStepData.destinationLatitude = intent.getStringExtra("destinationLatitude")
        rideStepData.fare = intent.getStringExtra("fare")
        rideStepData.discount = intent.getStringExtra("discount")
        rideStepData.finalFare = intent.getStringExtra("finalFare")

            step(context, rideStepData, "get_ride_request")

    }

    var apiKey = ""
    var placesClient: PlacesClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppSession.save(context, Constants.SCREEN_TYPE, "home_screen")

        val bundle = intent.extras
        if (bundle != null) {
            for (key in intent.extras!!.keySet()) {
                val value = intent.extras!![key]
                Log.i("intent", "Key: $key Value: $value")
            }
        }
        if (intent != null && intent.hasExtra("customer_name")) {
            newRideNotification = true
            rideRequest(intent)
            showLog("rideId", "Intent : mainActivity " + intent?.getStringExtra("ride_id"))
        }

        apiKey = getString(R.string.api_key)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
        // Create a new Places client instance.
        placesClient = Places.createClient(this)


        // register broadcast manager
        val localBroadcastManager = LocalBroadcastManager.getInstance(context)
        localBroadcastManager.registerReceiver(receiver, IntentFilter("your_action"))

        // Instance of LocationViewModel
        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)

        //Check weather Location/GPS is ON or OFF
        LocationUtil(this).turnGPSOn(object :
            LocationUtil.OnLocationOnListener {
            override fun locationStatus(isLocationOn: Boolean) {
                this@MainActivity.isGPSEnabled = isLocationOn
            }
        })

        setCurrentFragment(homeFragment)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            isOnHome = false
            when (it.itemId) {
                R.id.navigation_home -> {
                    setCurrentFragment(homeFragment)
                    isOnHome = true
                }
                R.id.navigation_earning -> setCurrentFragment(earningFragment)
                R.id.navigation_rides -> setCurrentFragment(rideFragment)
                R.id.navigation_account -> setCurrentFragment(accountFragment)
            }
            true
        }




        binding.ivBack.setOnClickListener {
            showHideOtp("hide")
        }

        setViewSize()
        editText1 = binding.etOtp1st
        editText2 = binding.etOtp2nd
        editText3 = binding.etOtp3rd
        editText4 = binding.etOtp4th

        editText1!!.setTextColor(Color.WHITE)
        editText1!!.hint = ""
        editText1!!.setBackgroundResource(R.drawable.after_otp_bg)

        //GenericTextWatcher here works only for moving to next EditText when a number is entered
        //first parameter is the current EditText and second parameter is next EditText
        editText1!!.addTextChangedListener(GenericTextWatcherOTP(editText1!!, editText2))
        editText2!!.addTextChangedListener(GenericTextWatcherOTP(editText2!!, editText3))
        editText3!!.addTextChangedListener(GenericTextWatcherOTP(editText3!!, editText4))
        editText4!!.addTextChangedListener(GenericTextWatcherOTP(editText4!!, null))

        //GenericKeyEvent here works for deleting the element and to switch back to previous EditText
        //first parameter is the current EditText and second parameter is previous EditText
        editText1!!.setOnKeyListener(GenericKeyEventOTP(editText1!!, null))
        editText2!!.setOnKeyListener(GenericKeyEventOTP(editText2!!, editText1))
        editText3!!.setOnKeyListener(GenericKeyEventOTP(editText3!!, editText2))
        editText4!!.setOnKeyListener(GenericKeyEventOTP(editText4!!, editText3))

        binding.btnVerify.setOnClickListener {
            val otpCode = (binding.etOtp1st.text).toString() + (binding.etOtp2nd.text).toString() +
                    (binding.etOtp3rd.text).toString() + (binding.etOtp4th.text).toString()
            otpCode.trim()
            onVerify(otpCode)
        }

        // mainViewModel.getProfile()

        setAcceptRideObserver()
        setNavigateToPickupPointObserver()
        startRideObserver()
        reachedToDestinationObserver()
        updateLocationObserver()
        getOngoingRideObserver()
        checkRideStatusObserve()
        declineRide()
        completeRideObserver()
       // getProfileObserver()
        endRideObserver()
        updateFCMObserver()
        updateFCMToken()
    }

    private fun updateFCMToken() {
        try {
            val oldFCM = AppSession.getValue(context, Constants.FCM_TOKEN)
            val newFCM = AppSession.getValue(context, Constants.NEW_FCM_TOKEN)
            if (oldFCM != null && newFCM != null) {
                if (!oldFCM.equals(newFCM)) {
                    mainViewModel.updateFCM(UpdateFCMToken(newFCM))
                } else {
                    showLog("updateFCMToken", "FCM All Ready Updated.")
                }
            } else {
                showLog("updateFCMToken", "/nOLD FCM - $oldFCM \nNEW FCM - $newFCM\n")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showLog("updateFCMToken", "Error - ${e.message}")
        }
    }

    private fun updateFCMObserver() {
        mainViewModel.updateFCMResponse.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        showLog("updateFCMToken", it.data.message)
                        val newFCM = AppSession.getValue(context, Constants.NEW_FCM_TOKEN)
                        AppSession.save(context, Constants.FCM_TOKEN, newFCM)
                    }
                }
                is NetworkResult.Error -> {
                    Log.d("updateFCMToken", "Error - " + it.message)
                }
                is NetworkResult.Loading -> {
                    showLoader()
                    Log.d("updateFCMToken", "Loading")
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()

        showLog("pipinpip", "MainActivity :onResume")
        // get on going ride
        if (!newRideNotification) {
            mainViewModel.getOngoingRide()
        }
        rideGoign(false)
        binding.bottomNavigationView.visibility = View.VISIBLE
    }

    fun onVerify(otpCode: String) {
        if (otpCode.length == 4) {
            showLoader()
            // check here code is same or not....
            Handler().postDelayed({
                hideLoader()
                val confirmOtp = AppSession.getValue(context, Constants.CONFIRM_OTP)!!

                showLog("showOTP", confirmOtp)
                if (confirmOtp == otpCode) {
                    showLog(TAG, "Right OTP")
                    startRideSheet(rideStepData)
                    showStartRideScreen = true
                    showHideOtp("hide")
                } else {
                    showToast("Wrong OTP")
                }
            }, 1000)

        } else {
            Toast.makeText(context, "Please enter valid otp.", Toast.LENGTH_LONG).show()
        }
    }

    // set edit views size
    private fun setViewSize() {
        val height = (CommonFunction.getScreenHeight() * 0.07).toInt()
        binding.etOtp1st.layoutParams.height = height
        binding.etOtp2nd.layoutParams.height = height
        binding.etOtp3rd.layoutParams.height = height
        binding.etOtp4th.layoutParams.height = height
    }

    private fun showHideOtp(code: String) {
        when (code) {
            "show" -> {
                binding.rlOtp.visibility = View.VISIBLE
                isOTPScreenVisible = binding.rlOtp.isVisible
            }
            "hide" -> {
                binding.rlOtp.visibility = View.GONE
            }
            else -> {
                showToast("This code ($code) not handle...")
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }

    // Observe LocationViewModel LiveData to get updated location
    private fun observeLocationUpdates() {
        locationViewModel.getLocationData.observe(this) {
            // get and set  current location
            mCurrentLatitude = it.latitude
            mCurrentLongitude = it.longitude
            location = it
            if (it != null) {
                val updateLocationRequest = UpdateLocationRequest()
                updateLocationRequest.driverlatitude = it.latitude
                updateLocationRequest.driverlongitude = it.longitude
                updateLocationRequest.angle = it.bearing
                mainViewModel.updateLocation(updateLocationRequest)
            }

            when (navigateStep) {

                "current_location" -> {
                    setCurrentLocation(mCurrentLatitude, mCurrentLongitude)
                }

                "navigate_to_pickup_point" -> {
                    routeToPickupPoint(LatLng(mCurrentLatitude, mCurrentLongitude), rideStepData)
                    getPointsDistance(
                        mCurrentLatitude,
                        mCurrentLongitude,
                        rideStepData.pickupLatitude!!.toDouble(),
                        rideStepData.pickupLongitude!!.toDouble(),
                        navigateStep
                    )
                }

                "navigate_to_drop_point" -> {
                    routeToPickupPoint(LatLng(mCurrentLatitude, mCurrentLongitude), rideStepData)
                    getPointsDistance(
                        mCurrentLatitude,
                        mCurrentLongitude,
                        rideStepData.destinationLatitude!!.toDouble(),
                        rideStepData.destinationLongitude!!.toDouble(),
                        navigateStep
                    )
                }

                else -> {
                    showToast("continue step is $")
                }
            }
        }
    }

    // onStart lifecycle of activity
    override fun onStart() {
        super.onStart()
        navigateStep = "current_location"
        startLocationUpdates()
    }

    /**
     * Initiate Location updated by checking Location/GPS settings is ON or OFF
     * Requesting permissions to read location.
     */
    private fun startLocationUpdates() {
        when {
            !isGPSEnabled -> {
                // info.text = getString(R.string.enable_gps)
            }

            isLocationPermissionsGranted() -> {
                observeLocationUpdates()
            }
            else -> {
                askLocationPermission()
            }
        }
    }

    private fun isLocationPermissionsGranted(): Boolean {
        return (EzPermission.isGranted(context, locationPermissions[0])
                && EzPermission.isGranted(context, locationPermissions[1]))
    }

    private fun askLocationPermission() {
        EzPermission
            .with(context)
            .permissions(locationPermissions[0], locationPermissions[1])
            .request { granted, denied, permanentlyDenied ->
                if (granted.contains(locationPermissions[0]) &&
                    granted.contains(locationPermissions[1])
                ) { // Granted
                    startLocationUpdates()

                } else if (denied.contains(locationPermissions[0]) ||
                    denied.contains(locationPermissions[1])
                ) { // Denied

                    showDeniedDialog()

                } else if (permanentlyDenied.contains(locationPermissions[0]) ||
                    permanentlyDenied.contains(locationPermissions[1])
                ) { // Permanently denied
                    showPermanentlyDeniedDialog()
                }

            }
    }

    private fun showPermanentlyDeniedDialog() {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(getString(R.string.title_permission_permanently_denied))
        dialog.setMessage(getString(R.string.message_permission_permanently_denied))
        dialog.setNegativeButton(getString(R.string.not_now)) { _, _ -> }
        dialog.setPositiveButton(getString(R.string.settings)) { _, _ ->
            startActivity(EzPermission.appDetailSettingsIntent(context))
        }
        dialog.setOnCancelListener { } //important
        dialog.show()
    }

    private fun showDeniedDialog() {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(getString(R.string.title_permission_denied))
        dialog.setMessage(getString(R.string.message_permission_denied))
        dialog.setNegativeButton(getString(R.string.cancel)) { _, _ -> }
        dialog.setPositiveButton(getString(R.string.allow)) { _, _ ->
            askLocationPermission()
        }
        dialog.setOnCancelListener { } //important
        dialog.show()
    }

    // On Activity Result for locations permissions updates
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == LOCATION_PERMISSION_REQUEST) {
                isGPSEnabled = true
                startLocationUpdates()
            }
        }
    }


    /*------------------------------------- steps flow-----------------------------------------*/

    // step 0
    // set marker on current location
    open fun setCurrentLocation(latitude: Double, longitude: Double) {
        if (homeFragment != null) {
            homeFragment.setCurrentLocation(latitude, longitude)
        }
    }

    // step 2c
    // show ride request sheet
    private fun showRideRequestSheet(rideStepData: RideStepData) {
        acceptRejectDialog = Dialog(context)
        acceptRejectDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        acceptRejectDialog!!.setContentView(R.layout.ride_bottom_sheet_layout)
        acceptRejectDialog!!.setCanceledOnTouchOutside(false)
        acceptRejectDialog!!.setCancelable(false)
        val tvCustomerName = acceptRejectDialog!!.findViewById<TextView>(R.id.tvCustomerName)
        val tvRating = acceptRejectDialog!!.findViewById<TextView>(R.id.tvRating)
        val tvRideFare = acceptRejectDialog!!.findViewById<TextView>(R.id.tvRideFare)
        val tvCoins = acceptRejectDialog!!.findViewById<TextView>(R.id.tvRideCoin)
        val ivRideCoin = acceptRejectDialog!!.findViewById<ImageView>(R.id.ivRideCoin)
        val tvPickupAddress = acceptRejectDialog!!.findViewById<TextView>(R.id.tvPickupAddress)
        val tvDropAddress = acceptRejectDialog!!.findViewById<TextView>(R.id.tvDropAddress)
        val tvAccept = acceptRejectDialog!!.findViewById<TextView>(R.id.tvAccept)
        val tvDecline = acceptRejectDialog!!.findViewById<TextView>(R.id.tvDecline)

        /*    val ivCustomerProfile = acceptRejectDialog!!.findViewById<ImageView>(R.id.ivCustomerProfile)
            try {
                val imageUrl = "${Constants.BASE_URL}${rideStepData.selfie}"
                Glide.with(this).load(imageUrl).into(ivCustomerProfile)
            } catch (e: Exception) {
                e.printStackTrace()
            }*/

        tvCustomerName.text = rideStepData.customer_name
        tvPickupAddress.text = rideStepData.pickupLocation
        tvDropAddress.text = rideStepData.destinationLocation

        try {
            val discount = rideStepData.discount!!.toInt()
            val finalFare = rideStepData.finalFare!!.toInt()
            showLog("discount", "discount - $discount, finalFare - $finalFare")
            if (discount > 0) {
                tvRideFare.text = "Rs - ${rideStepData.finalFare} + "
                tvCoins.text = "${rideStepData.discount}"
                tvCoins.visibility = View.VISIBLE
                ivRideCoin.visibility = View.VISIBLE
            } else {
                tvRideFare.text = "Rs - ${rideStepData.fare}"
                tvCoins.visibility = View.GONE
                ivRideCoin.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        tvRating.text = rideStepData.customer_rating.toString()
        val ivStartRating_1 = acceptRejectDialog!!.findViewById<ImageView>(R.id.ivStartRating_1)
        val ivStartRating_2 = acceptRejectDialog!!.findViewById<ImageView>(R.id.ivStartRating_2)
        val ivStartRating_3 = acceptRejectDialog!!.findViewById<ImageView>(R.id.ivStartRating_3)
        val ivStartRating_4 = acceptRejectDialog!!.findViewById<ImageView>(R.id.ivStartRating_4)
        val ivStartRating_5 = acceptRejectDialog!!.findViewById<ImageView>(R.id.ivStartRating_5)

        val ivRating = ArrayList<ImageView>()
        ivRating.add(ivStartRating_1)
        ivRating.add(ivStartRating_2)
        ivRating.add(ivStartRating_3)
        ivRating.add(ivStartRating_4)
        ivRating.add(ivStartRating_5)
try {

    val rating = rideStepData.customer_rating!!.toString().toFloat().toInt()
    //showLog("rating" ,"rating $rating")

    for (i in 0 until rating) {
        ivRating[i].setImageResource(R.drawable.ic_star)
    }

}
catch (e:Exception)
{
    e.printStackTrace()
}
        tvDecline.setOnClickListener {
            cancelNotification()
            if (rideStepData.ride_id != null) {
                mainViewModel.declineRide(RideRequest(rideStepData.ride_id!!))
            }

        }

        tvAccept.setOnClickListener {
            cancelNotification()
            if (rideStepData.ride_id != null) {
                acceptRide(rideStepData, acceptRejectDialog!!)
                showLog("rideId", " " + rideStepData.ride_id)
            }
        }

        acceptRejectDialog!!.show()
        acceptRejectDialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        acceptRejectDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        acceptRejectDialog!!.window!!.attributes.windowAnimations = R.style.DialogAnimation
        acceptRejectDialog!!.window!!.setGravity(Gravity.BOTTOM)
    }

    // step 3
    // show marker on current and pickup location
    private fun navigateToPickupPointSheet(rideStepData: RideStepData) {
        showLog("step", "step - navigateToPickupDialog")
        navigateToPickupDialog = Dialog(context)
        navigateToPickupDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        navigateToPickupDialog?.setContentView(R.layout.navigate_pickup_point_bottom_sheet_layout)
        navigateToPickupDialog?.setCanceledOnTouchOutside(false)
        navigateToPickupDialog?.setCancelable(false)
        val tvCustomerName = navigateToPickupDialog?.findViewById<TextView>(R.id.tvCustomerName)
        val tvRating = navigateToPickupDialog?.findViewById<TextView>(R.id.tvRating)
        val tvRideFare = navigateToPickupDialog?.findViewById<TextView>(R.id.tvRideFare)
        val tvPickupAddress = navigateToPickupDialog?.findViewById<TextView>(R.id.tvPickupAddress)
        val tvDropAddress = navigateToPickupDialog?.findViewById<TextView>(R.id.tvDropAddress)
        val tvNavigate = navigateToPickupDialog?.findViewById<TextView>(R.id.tvNavigate)
        /*   val ivCustomerProfile =
               navigateToPickupDialog!!.findViewById<ImageView>(R.id.ivCustomerProfile)
           try {
               val imageUrl = "${AppSession.getValue(context, Constants.BASE_URL)}"
               showLog("image", "imageUrl - " + imageUrl)
               Glide.with(this).load(imageUrl)
                   .placeholder(getDrawable(R.drawable.profile_icon))
                   .into(ivCustomerProfile)
           } catch (e: Exception) {
               e.printStackTrace()
           }*/

        tvCustomerName?.text = rideStepData.customer_name
        tvPickupAddress?.text = rideStepData.pickupLocation
        tvDropAddress?.text = rideStepData.destinationLocation
        tvRideFare?.text = "Rs - ${rideStepData.fare}"
        tvRating?.text = rideStepData.customer_rating

        val ivStartRating_1 = navigateToPickupDialog!!.findViewById<ImageView>(R.id.ivStartRating_1)
        val ivStartRating_2 = navigateToPickupDialog!!.findViewById<ImageView>(R.id.ivStartRating_2)
        val ivStartRating_3 = navigateToPickupDialog!!.findViewById<ImageView>(R.id.ivStartRating_3)
        val ivStartRating_4 = navigateToPickupDialog!!.findViewById<ImageView>(R.id.ivStartRating_4)
        val ivStartRating_5 = navigateToPickupDialog!!.findViewById<ImageView>(R.id.ivStartRating_5)

        val ivRating = ArrayList<ImageView>()
        ivRating.add(ivStartRating_1)
        ivRating.add(ivStartRating_2)
        ivRating.add(ivStartRating_3)
        ivRating.add(ivStartRating_4)
        ivRating.add(ivStartRating_5)

        val rating = rideStepData.customer_rating!!.toString().toFloat().toInt()
        //showLog("rating" ,"rating $rating")

        for (i in 0 until rating) {
            ivRating[i].setImageResource(R.drawable.ic_star)
        }

        tvNavigate?.setOnClickListener {
//            navigateStep = "navigate_to_pickup_point"
//            getPointsDistance(mCurrentLatitude, mCurrentLongitude, rideStepData.pickupLatitude!!.toDouble(), rideStepData.pickupLongitude!!.toDouble(), navigateStep)
//            routeToPickupPoint(LatLng(mCurrentLatitude, mCurrentLongitude), rideStepData)
            navigateToPickupDialog?.dismiss()
        }

        navigateToPickupDialog?.show()
        navigateToPickupDialog?.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        navigateToPickupDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        navigateToPickupDialog?.window!!.attributes.windowAnimations = R.style.DialogAnimation
        navigateToPickupDialog?.window!!.setGravity(Gravity.BOTTOM)
        showLog("step", "step - navigateToPickupDialog end")
    }

    // step 4
    // get distance between current to pickup location
    private fun getPointsDistance(
        mCurrentLatitude: Double,
        mCurrentLongitude: Double,
        targetLatitude: Double?,
        targetLongitude: Double?,
        navigateStep: String
    ) {
//        val sPosition = LatLng(mCurrentLatitude, mCurrentLongitude)
//        val ePosition = LatLng(targetLatitude!!, targetLongitude!!)
        val markerOptions = MarkerOptions()
        markerOptions.title("Distance")

        val results = FloatArray(10)

        Location.distanceBetween(
            mCurrentLatitude,
            mCurrentLongitude,
            targetLatitude!!,
            targetLongitude!!,
            results
        )

        val distanceKm = String.format("%.1f", (results[0] / 1000)).toFloat()
        val distanceM = (distanceKm * 1000).toInt()
        showLog(TAG, "distanceM - $distanceM")

      //  rideGoign(true)
        when (navigateStep) {
            "navigate_to_pickup_point" -> {
                mainViewModel.navigateToPickupPoint(distanceM)
//                 mainViewModel.navigateToPickupPoint(NavigateToPickupPointRequest(50))
            }
            "navigate_to_drop_point" -> {
                mainViewModel.reachedToDestination(distanceM)
//                 mainViewModel.reachedToDestination(NavigateToPickupPointRequest(50))
            }
            else -> {
                showToast("Navigation Step - $navigateStep")
            }
        }
    }

    // step 4
    // set route current to pickup point
    private fun routeToPickupPoint(currentLocation: LatLng, rideStepData: RideStepData) {
        if (homeFragment != null) {
            if (location != null) {
                homeFragment.createRouteRide(currentLocation, rideStepData, apiKey, location)
            }
        }
    }

    // step 5
    // show enter otp sheet
    private fun showEnterOTPSheet(rideStepData: RideStepData) {
        enterOTPDialog = Dialog(context)
        enterOTPDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        enterOTPDialog?.setContentView(R.layout.otp_bottom_sheet_layout)
        enterOTPDialog?.setCanceledOnTouchOutside(false)
        enterOTPDialog?.setCancelable(false)

        val tvCustomerName = enterOTPDialog?.findViewById<TextView>(R.id.tvCustomerName)
        val tvRating = enterOTPDialog?.findViewById<TextView>(R.id.tvRating)
        val tvRideFare = enterOTPDialog?.findViewById<TextView>(R.id.tvRideFare)
        val tvPickupAddress = enterOTPDialog?.findViewById<TextView>(R.id.tvPickupAddress)
        val tvDropAddress = enterOTPDialog?.findViewById<TextView>(R.id.tvDropAddress)
        val tvEnterOtp = enterOTPDialog?.findViewById<TextView>(R.id.tvEnterOtp)

        val ivStartRating_1 = enterOTPDialog!!.findViewById<ImageView>(R.id.ivStartRating_1)
        val ivStartRating_2 = enterOTPDialog!!.findViewById<ImageView>(R.id.ivStartRating_2)
        val ivStartRating_3 = enterOTPDialog!!.findViewById<ImageView>(R.id.ivStartRating_3)
        val ivStartRating_4 = enterOTPDialog!!.findViewById<ImageView>(R.id.ivStartRating_4)
        val ivStartRating_5 = enterOTPDialog!!.findViewById<ImageView>(R.id.ivStartRating_5)

        val ivRating = ArrayList<ImageView>()
        ivRating.add(ivStartRating_1)
        ivRating.add(ivStartRating_2)
        ivRating.add(ivStartRating_3)
        ivRating.add(ivStartRating_4)
        ivRating.add(ivStartRating_5)

        try {
            val rating = rideStepData.customer_rating!!.toString().toFloat().toInt()
            showLog("rating", "rating $rating")

            for (i in 0 until rating) {
                ivRating[i].setImageResource(R.drawable.ic_star)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

        tvCustomerName?.text = rideStepData.customer_name
        tvRating?.text = rideStepData.customer_rating.toString()
        tvRideFare?.text = "Rs - ${rideStepData.fare}"
        tvPickupAddress?.text = rideStepData.pickupLocation
        tvDropAddress?.text = rideStepData.destinationLocation
        tvEnterOtp?.setOnClickListener {
            // show OTP UI
            showHideOtp("show")
            enterOTPDialog?.dismiss()
        }

        enterOTPDialog?.show()
        enterOTPDialog?.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        enterOTPDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        enterOTPDialog?.window!!.attributes.windowAnimations = R.style.DialogAnimation
        enterOTPDialog?.window!!.setGravity(Gravity.BOTTOM)
    }

    private fun startRideSheet(rideStepData: RideStepData) {
        startRideDialog = Dialog(this)
        startRideDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        startRideDialog?.setContentView(R.layout.start_ride_bottom_sheet_layout)
        startRideDialog?.setCanceledOnTouchOutside(false)
        startRideDialog?.setCancelable(false)
        val tvCustomerName = startRideDialog?.findViewById<TextView>(R.id.tvCustomerName)
        val tvRating = startRideDialog?.findViewById<TextView>(R.id.tvRating)
        val tvRideFare = startRideDialog?.findViewById<TextView>(R.id.tvRideFare)
        val tvPickupAddress = startRideDialog?.findViewById<TextView>(R.id.tvPickupAddress)
        val tvDropAddress = startRideDialog?.findViewById<TextView>(R.id.tvDropAddress)
        val tvStartRide = startRideDialog?.findViewById<SlidingButton>(R.id.tvStartRide)

        tvCustomerName?.text = rideStepData.customer_name
        tvRating?.text = rideStepData.customer_rating
        tvRideFare?.text = "Rs - ${rideStepData.fare}"
        tvPickupAddress?.text = rideStepData.pickupLocation
        tvDropAddress?.text = rideStepData.destinationLocation

        val ivStartRating_1 = startRideDialog!!.findViewById<ImageView>(R.id.ivStartRating_1)
        val ivStartRating_2 = startRideDialog!!.findViewById<ImageView>(R.id.ivStartRating_2)
        val ivStartRating_3 = startRideDialog!!.findViewById<ImageView>(R.id.ivStartRating_3)
        val ivStartRating_4 = startRideDialog!!.findViewById<ImageView>(R.id.ivStartRating_4)
        val ivStartRating_5 = startRideDialog!!.findViewById<ImageView>(R.id.ivStartRating_5)

        val ivRating = ArrayList<ImageView>()
        ivRating.add(ivStartRating_1)
        ivRating.add(ivStartRating_2)
        ivRating.add(ivStartRating_3)
        ivRating.add(ivStartRating_4)
        ivRating.add(ivStartRating_5)

        try {
            val rating = rideStepData.customer_rating!!.toString().toFloat().toInt()
            //showLog("rating" ,"rating $rating")

            for (i in 0 until rating) {
                ivRating[i].setImageResource(R.drawable.ic_star)
            }

        }catch (e:Exception){
            e.printStackTrace()
        }

        /*tvStartRide.setOnClickListener {
            if (rideStepData.ride_id != null) {
                startRide(rideStepData.ride_id!!, dialog)
            }
        }*/

        tvStartRide?.setOnStateChangeListener { active ->
            //or using `object : SlidingButton.OnStateChangeListener` instead of lambda
            if (rideStepData.ride_id != null) {
                mainViewModel.startRide(RideRequest(rideStepData.ride_id!!))
                showStartRideScreen = false
                isOTPScreenVisible = false
                //startRide(rideStepData.ride_id!!, startRideDialog!!)
            }
        }


        startRideDialog?.show()
        startRideDialog?.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        startRideDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        startRideDialog?.window!!.attributes.windowAnimations = R.style.DialogAnimation
        startRideDialog?.window!!.setGravity(Gravity.BOTTOM)
    }

    // step 3
    // show marker on current and pickup location
    private fun navigateToDropPointSheet(rideStepData: RideStepData) {
        navigateToDropPointDialog = Dialog(context)
        navigateToDropPointDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        navigateToDropPointDialog?.setContentView(R.layout.navigate_drop_point_bottom_sheet_layout)
        navigateToDropPointDialog?.setCanceledOnTouchOutside(false)
        navigateToDropPointDialog?.setCancelable(false)
        val tvCustomerName = navigateToDropPointDialog?.findViewById<TextView>(R.id.tvCustomerName)
        val tvRating = navigateToDropPointDialog?.findViewById<TextView>(R.id.tvRating)
        val tvRideFare = navigateToDropPointDialog?.findViewById<TextView>(R.id.tvRideFare)
        val tvPickupAddress =
            navigateToDropPointDialog?.findViewById<TextView>(R.id.tvPickupAddress)
        val tvDropAddress = navigateToDropPointDialog?.findViewById<TextView>(R.id.tvDropAddress)
        val tvNavigate = navigateToDropPointDialog?.findViewById<TextView>(R.id.tvNavigate)

        tvCustomerName?.text = rideStepData.customer_name
        tvPickupAddress?.text = rideStepData.pickupLocation
        tvDropAddress?.text = rideStepData.destinationLocation
        tvRideFare?.text = "Rs - ${rideStepData.fare}"
        tvRating?.text = rideStepData.customer_rating

        val ivStartRating_1 =
            navigateToDropPointDialog!!.findViewById<ImageView>(R.id.ivStartRating_1)
        val ivStartRating_2 =
            navigateToDropPointDialog!!.findViewById<ImageView>(R.id.ivStartRating_2)
        val ivStartRating_3 =
            navigateToDropPointDialog!!.findViewById<ImageView>(R.id.ivStartRating_3)
        val ivStartRating_4 =
            navigateToDropPointDialog!!.findViewById<ImageView>(R.id.ivStartRating_4)
        val ivStartRating_5 =
            navigateToDropPointDialog!!.findViewById<ImageView>(R.id.ivStartRating_5)

        val ivRating = ArrayList<ImageView>()
        ivRating.add(ivStartRating_1)
        ivRating.add(ivStartRating_2)
        ivRating.add(ivStartRating_3)
        ivRating.add(ivStartRating_4)
        ivRating.add(ivStartRating_5)

        val rating = rideStepData.customer_rating!!.toString().toFloat().toInt()
        //showLog("rating" ,"rating $rating")

        for (i in 0 until rating) {
            ivRating[i].setImageResource(R.drawable.ic_star)
        }

        tvNavigate?.setOnClickListener {
//            navigateStep = "navigate_to_drop_point"
//            getPointsDistance(mCurrentLatitude, mCurrentLongitude, rideStepData.destinationLatitude!!.toDouble(), rideStepData.destinationLongitude!!.toDouble(), navigateStep)
//            routeToPickupPoint(LatLng(mCurrentLatitude, mCurrentLongitude), rideStepData)
            navigateToDropPointDialog?.dismiss()
        }

        navigateToDropPointDialog?.show()
        navigateToDropPointDialog?.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        navigateToDropPointDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        navigateToDropPointDialog?.window!!.attributes.windowAnimations = R.style.DialogAnimation
        navigateToDropPointDialog?.window!!.setGravity(Gravity.BOTTOM)
    }


    val timeInterval_handler = Handler()
    var runnable: Runnable = object : Runnable {
        override fun run() {
            timeInterval_handler.removeCallbacks(this)
            if (true) {

                showLog("handler", "handler start")
                val id = AppSession.getValue(context, Constants.RIDE_ID)
                if (id != null) {
                    timeInterval_handler.postDelayed(this, 5000)
                    mainViewModel.checkRideStatus(id)
                }
            }
        }
    }

    private fun checkRideStatusObserve() {
        mainViewModel.checkRideStatusResponse.observe(this) {
            // hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.d("checkRideStatusResponse", "success : " + it.data.toString())
                        if (it.data.success) {
                            rideGoign(false)
                            timeInterval_handler.removeCallbacks(runnable)
                            step(context, rideStepData,"make_payment")
                        }
                    }
                }
                is NetworkResult.Error -> {
                    Log.d("checkRideStatusResponse", "Error - " + it.message)
                }
                is NetworkResult.Loading -> {
                    //   showLoader()
                    Log.d("checkRideStatusResponse", "Loading")
                }
            }
        }
    }


    private fun endOngoingRideSheet(message: String) {
        endOngoing = Dialog(context)
        endOngoing?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        endOngoing?.setContentView(R.layout.end_ride_waiting_bottom_sheet_layout)
        endOngoing?.setCanceledOnTouchOutside(false)
        endOngoing?.setCancelable(false)
        val tvMessage = endOngoing?.findViewById<TextView>(R.id.tvMessage)
        val tvCancel = endOngoing?.findViewById<TextView>(R.id.tvCancel)

        tvMessage!!.text = message
        tvCancel!!.setOnClickListener {
            timeInterval_handler.removeCallbacks(runnable)
            endOngoing?.dismiss()
        }

        timeInterval_handler.postDelayed(runnable, 100)

        endOngoing?.show()
        endOngoing?.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        endOngoing?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        endOngoing?.window!!.attributes.windowAnimations = R.style.DialogAnimation
        endOngoing?.window!!.setGravity(Gravity.BOTTOM)
    }


    private fun endRideSheet(rideStepData: RideStepData) {
        showEndRideScreen = true
        endRideDialog = Dialog(context)
        endRideDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        endRideDialog?.setContentView(R.layout.end_ride_bottom_sheet_layout)
        endRideDialog?.setCanceledOnTouchOutside(false)
        endRideDialog?.setCancelable(false)

        val tvCustomerName = endRideDialog?.findViewById<TextView>(R.id.tvCustomerName)
        val tvRating = endRideDialog?.findViewById<TextView>(R.id.tvRating)
        val tvRideFare = endRideDialog?.findViewById<TextView>(R.id.tvRideFare)
        val tvPickupAddress = endRideDialog?.findViewById<TextView>(R.id.tvPickupAddress)
        val tvDropAddress = endRideDialog?.findViewById<TextView>(R.id.tvDropAddress)
        val tvEndRide = endRideDialog?.findViewById<SlidingButton>(R.id.tvEndRide)

        tvCustomerName?.text = rideStepData.customer_name
        tvPickupAddress?.text = rideStepData.pickupLocation
        tvDropAddress?.text = rideStepData.destinationLocation
        tvRideFare?.text = "Rs - ${rideStepData.fare}"
        tvRating?.text = rideStepData.customer_rating

        val ivStartRating_1 = endRideDialog!!.findViewById<ImageView>(R.id.ivStartRating_1)
        val ivStartRating_2 = endRideDialog!!.findViewById<ImageView>(R.id.ivStartRating_2)
        val ivStartRating_3 = endRideDialog!!.findViewById<ImageView>(R.id.ivStartRating_3)
        val ivStartRating_4 = endRideDialog!!.findViewById<ImageView>(R.id.ivStartRating_4)
        val ivStartRating_5 = endRideDialog!!.findViewById<ImageView>(R.id.ivStartRating_5)

        val ivRating = ArrayList<ImageView>()
        ivRating.add(ivStartRating_1)
        ivRating.add(ivStartRating_2)
        ivRating.add(ivStartRating_3)
        ivRating.add(ivStartRating_4)
        ivRating.add(ivStartRating_5)

        try {
            val rating = rideStepData.customer_rating!!.toString().toFloat().toInt()
            //showLog("rating" ,"rating $rating")

            for (i in 0 until rating) {
                ivRating[i].setImageResource(R.drawable.ic_star)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }


        /*tvEndRide.setOnClickListener {
            if (rideStepData.ride_id != null) {
                completeRide(rideStepData.ride_id!!, dialog)
            }
        }*/
        tvEndRide?.setOnStateChangeListener { active ->
            //or using `object : SlidingButton.OnStateChangeListener` instead of lambda
            if (rideStepData.ride_id != null) {
                mainViewModel.completeRide(RideRequest(rideStepData.ride_id!!))
                //completeRide(rideStepData.ride_id!!, dialog)
            }
        }

        endRideDialog?.show()
        endRideDialog?.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        endRideDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        endRideDialog?.window!!.attributes.windowAnimations = R.style.DialogAnimation
        endRideDialog?.window!!.setGravity(Gravity.BOTTOM)
    }

    //----------------------------------- APIs -----------------------------------------

    // for on going ride
    private fun getOngoingRideObserver() {
        mainViewModel.getOngoingRideResponse.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        if (it.data != null) {
                            Log.d(TAG, it.data.toString())
                            val rideData = it.data.ride
                            rideStepData.customer_name = rideData.customerId.fullName
                            rideStepData.customer_id = rideData.customerId._id
                            rideStepData.customer_number = rideData.customer_number
                            rideStepData.selfie = rideData.customerId.profileImage
                            rideStepData.customer_rating = rideData.customer_rating
                            rideStepData.ride_id = rideData.rideId
                            rideStepData.status = rideData.status
                            rideStepData.fare = rideData.fare.toString()
                            rideStepData.discount = rideData.discount.toString()
                            rideStepData.finalFare = rideData.finalFare.toString()
                            rideStepData.numberOfPassengers = rideData.numberOfPassengers.toString()
                            rideStepData.pickupLocation = rideData.pickupLocation
                            rideStepData.pickupLatitude = rideData.pickupLatitude.toString()
                            rideStepData.pickupLongitude = rideData.pickupLongitude.toString()
                            rideStepData.destinationLocation = rideData.destinationLocation
                            rideStepData.destinationLatitude =
                                rideData.destinationLatitude.toString()
                            rideStepData.destinationLongitude =
                                rideData.destinationLongitude.toString()
                            showLog("ride status", "ride status - " + rideData.status)
                            if (rideData.status.equals("Accepted")) {
                                if (showStartRideScreen) {
                                    startRideDialog?.show()
                                } else if (!isOTPScreenVisible) {
                                    step(context, rideStepData, "navigate_to_pickup_point")
                                }

                            } else if (rideData.status.equals("Ongoing")) {
                                if (showEndRideScreen) {
                                    endRideDialog?.show()
                                } else {
                                    step(context, rideStepData, "navigate_to_drop_point")
                                }
                            } else if (rideData.status.equals("Payment")) {
                                navigateStep = "current_location"
                                step(context, rideStepData, "make_payment")
                            } else {
                                //showToast("Under production work for ride status ${rideData.status}")
                                showLog(
                                    TAG,
                                    "Under production work for ride status ${rideData.status}"
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        navigateStep = "current_location"
                    }
                }
                is NetworkResult.Error -> {
                    Log.d(TAG, "Error - " + it.message)
                }
                is NetworkResult.Loading -> {
                    Log.d(TAG, "Loading")
                }
            }
        }
    }

    // step 2
    // for decline ride
    private fun declineRide() {
        mainViewModel.declineRideResponse.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.d("DeclineRes", "success : " + it.data.toString())
                        rideGoign(false)
                        acceptRejectDialog?.dismiss()
                        newRideNotification = false
                        isWindowShow = false
                    }
                }
                is NetworkResult.Error -> {
                    Log.d("DeclineRes", "Error - " + it.message)
                }
                is NetworkResult.Loading -> {
                    showLoader()
                    Log.d("DeclineRes", "Loading")
                }
            }
        }
    }

    // step 2
    // for accept ride
    private fun acceptRide(rideStepData: RideStepData, dialog: Dialog) {
        mainViewModel.acceptRide(RideRequest(rideStepData.ride_id!!))

    }

    private fun setAcceptRideObserver() {
        mainViewModel.acceptRideResponse.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data.toString())
                    if (it.data != null && it.data.success) {
                        rideStepData.confirmOtp = it.data.confirmOtp
                        showLog("showOTP", it.data.confirmOtp.toString())
                        rideStepData.selfie = it.data.selfie
                        AppSession.save(
                            context,
                            Constants.CONFIRM_OTP,
                            it.data.confirmOtp.toString()
                        )
                        step(context, rideStepData, "navigate_to_pickup_point")

                    } else {
                        showToast(it.data?.message)
                    }
                    acceptRejectDialog?.dismiss()
                    newRideNotification = false
                }
                is NetworkResult.Error -> {
                    Log.d(TAG, "Error - " + it.message)
                }
                is NetworkResult.Loading -> {
                    showLoader()
                    Log.d(TAG, "Loading")
                }
            }
        }
    }

    // step 4
    // for get duration and distance
//    private fun navigateToPickupPointApi(distance: Int) {
//        mainViewModel.navigateToPickupPoint(NavigateToPickupPointRequest(distance))
//    }

    private fun setNavigateToPickupPointObserver() {
        var isResponse = true
        mainViewModel.navigateToPickupPointResponse.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, "Success - " + it.data)
                    if (homeFragment != null) {
                        homeFragment.setTime(it.data!!.message)
                    }
                    if (it.data!!.nextScreen != null && it.data!!.nextScreen.equals("enter_otp")) {
                        if (isResponse) {
                            isResponse = false
                            step(context, rideStepData, "enter_otp")
                        }
                    }
                }
                is NetworkResult.Error -> {
                    Log.d(TAG, "Error - " + it.message)
                }
                is NetworkResult.Loading -> {
                    Log.d(TAG, "Loading")
                }
            }
        }
    }

    // step 5
    // for start ride
    private fun startRideObserver() {


        mainViewModel.startRideResponse.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        showLog(TAG, it.data.toString())
                        step(context, rideStepData, "navigate_to_drop_point")
                        startRideDialog?.dismiss()
                    }
                }
                is NetworkResult.Error -> {
                    Log.d(TAG, "Error - " + it.message)
                }
                is NetworkResult.Loading -> {
                    showLoader()
                    Log.d(TAG, "Loading")
                }
            }
        }
    }

    // step 4
    // for get duration and distance
    // for get time
    private fun reachedToDestinationObserver() {
        mainViewModel.reachedToDestinationResponse.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    if (homeFragment != null) {
                        homeFragment.setTime(it.data!!.message)
                    }
                    if (it.data!!.nextScreen != null && it.data!!.nextScreen.equals("end_ride")) {
                        step(context, rideStepData, "end_ride")
                    }
                }
                is NetworkResult.Error -> {
                    Log.d(TAG, "Error - " + it.message)
                }
                is NetworkResult.Loading -> {
                    Log.d(TAG, "Loading")
                }
                else -> {}
            }
        }
    }

    // step 5
    // for start ride
    private fun completeRideObserver() {
        mainViewModel.completeRideResponse.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.d(TAG, it.data.toString())
                        showToast(it.data.message)
                        showEndRideScreen = false
                        step(context, rideStepData, "make_payment")
                        endRideDialog?.dismiss()
                    }
                }
                is NetworkResult.Error -> {
                    Log.d(TAG, "Error - " + it.message)
                    endRideDialog?.dismiss()
                    rideGoign(true)
                }
                is NetworkResult.Loading -> {
                    showLoader()
                    Log.d(TAG, "Loading")
                }
            }
        }
    }

    private fun updateLocationObserver() {

        mainViewModel.updateLocationResponse.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.i(TAG, "Success - " + it.data)
                    }
                }
                is NetworkResult.Error -> {
                    Log.i(TAG, "Error - " + it.message)

                }
                is NetworkResult.Loading -> {
                    Log.i(TAG, "Loading")
                }
                else -> {}
            }
        }
    }

    private fun endRideObserver() {
        mainViewModel.endRideResponse.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.i("ride_end", "Success - " + it.data)
                        endOngoingRideSheet(it.data.message)
                    }
                }
                is NetworkResult.Error -> {
                    Log.i("ride_end", "Error - " + it.message)

                }
                is NetworkResult.Loading -> {
                    Log.i("ride_end", "Loading")
                }
            }
        }
    }


  /*  private fun getProfileObserver() {
        mainViewModel.getProfileResponse.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.d("profile", it.data.toString())
                        val userData = it.data.data
                        *//*val vehicleData = it.data.vehicle
                        if (vehicleData != null){
                            AppSession.save(context,Constants.VEHICLE_ID, vehicleData.get(0)._id)
                           // AppSession.save(context,Constants.VEHICLE_IMAGE, vehicleData.get(0).uploadVehicleImage)
                        }*//*

                    }
                }
                is NetworkResult.Error -> {
                    Log.d("profile", "Error - " + it.message)
                }
                is NetworkResult.Loading -> {
                    Log.d("profile", "Loading")
                }
            }
        }
    }
*/

    fun rideGoign(isRideGoing: Boolean) {
        if (isRideGoing) {
            isWindowShow = true
            homeFragment.ivCall!!.visibility = View.VISIBLE
            homeFragment.ivEndRide!!.visibility = View.VISIBLE
            homeFragment.ivCall!!.setOnClickListener {
                makeCall(rideStepData.customer_number.toString())
            }
        } else {
            isWindowShow = false
            homeFragment.ivCall!!.visibility = View.GONE
            homeFragment.ivEndRide!!.visibility = View.GONE
        }
    }


    //--------------------------------- manage steps -----------------------------------

    fun step(context: Context, rideStepData: RideStepData, step: String) {
        AppSession.save(context, Constants.RIDE_ID, rideStepData.ride_id.toString().trim())
        showLog("step", "step - $step")
        if (step.equals("get_ride_request")) {
            rideGoign(false)
        }

        homeFragment.ivEndRide!!.setOnActiveListener {
             showToast("Activated")
            if (rideStepData.ride_id != null) {
                 mainViewModel.endRide(RideRequest(rideStepData.ride_id!!))
                 showLog("id_ride", "rideStepData.ride_id!! - ${rideStepData.ride_id!!}")
            }
        }

        when (step) {
            // step 1
            "get_ride_request" -> {
                showLog(TAG, "get_ride_request - show ride request sheet")
                showRideRequestSheet(rideStepData)
            }

            // step 2
            "navigate_to_pickup_point" -> {
                // showLog(TAG, "navigate_to_pickup_point - show marker on current and pickup location get status for reach pickup location")
                // continues till reach the pickup point
                // navigateToPickupPointSheet(rideStepData)

                navigateStep = "navigate_to_pickup_point"
                getPointsDistance(
                    mCurrentLatitude,
                    mCurrentLongitude,
                    rideStepData.pickupLatitude!!.toDouble(),
                    rideStepData.pickupLongitude!!.toDouble(),
                    step
                )
                routeToPickupPoint(LatLng(mCurrentLatitude, mCurrentLongitude), rideStepData)
            }

            // step 3
            "enter_otp" -> {
                showLog(TAG, "enter_otp - show enter otp sheet")
                navigateStep = "current_location"
                startLocationUpdates()
                showEnterOTPSheet(rideStepData)
            }

            // step 4
            "navigate_to_drop_point" -> {
                rideGoign(true)
                // showLog(TAG,"navigate_to_drop_point - show marker on current and drop location get status for reach drop location")
                // continues till reach the drop point
                // navigateToDropPointSheet(rideStepData)

                navigateStep = "navigate_to_drop_point"
                getPointsDistance(
                    mCurrentLatitude,
                    mCurrentLongitude,
                    rideStepData.destinationLatitude!!.toDouble(),
                    rideStepData.destinationLongitude!!.toDouble(),
                    step
                )
                routeToPickupPoint(LatLng(mCurrentLatitude, mCurrentLongitude), rideStepData)

            }

            // step 5
            "end_ride" -> {
                // Toast.makeText(context, "end_ride - show end ride sheet", Toast.LENGTH_SHORT).show()
                navigateStep = "current_location"
                startLocationUpdates()
                endRideSheet(rideStepData)

            }

            // step 6
            "make_payment" -> {
                isWindowShow = false
                val intent = Intent(context, RidePaymentActivity::class.java)
                intent.putExtra("date_time", "date and time needed.")
                intent.putExtra("pickupLocation", rideStepData.pickupLocation)
                intent.putExtra("destinationLocation", rideStepData.destinationLocation)
                intent.putExtra("fare", rideStepData.fare)
                intent.putExtra("rideId", rideStepData.ride_id)
                startActivity(intent)
                navigateStep = "current_location"
                startLocationUpdates()
            }

            // any thing else
            else -> {
                rideGoign(false)
                Toast.makeText(
                    context,
                    "($step) - Not match any step please check flow again...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroy() {
        // Unregister broadcast
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        timeInterval_handler.removeCallbacks(runnable)
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        timeInterval_handler.removeCallbacks(runnable)
        dismissDialogs()
    }

    override fun onPause() {
        super.onPause()
        dismissDialogs()
    }

    fun dismissDialogs() {
        navigateToPickupDialog?.dismiss()
        enterOTPDialog?.dismiss()
        startRideDialog?.dismiss()
        navigateToDropPointDialog?.dismiss()
        endRideDialog?.dismiss()
        endOngoing?.dismiss()
    }

    override fun onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
        pressedTime = System.currentTimeMillis()
    }

    fun enterPIPMode() {
        val d: Display = windowManager.defaultDisplay
        val p = Point()
        d.getSize(p)
        val width: Int = p.x
        val height: Int = p.y

        val ratio = Rational(width, height)
        var pip_Builder: PictureInPictureParams.Builder? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pip_Builder = PictureInPictureParams.Builder()
            pip_Builder.setAspectRatio(ratio).build()
            enterPictureInPictureMode(pip_Builder.build())
        }
    }

    fun callPictureInPicture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.bottomNavigationView.visibility = View.GONE
            enterPIPMode()
        }
    }
}