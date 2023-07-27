package com.suviridedriver.ui.ride_payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.suviridedriver.R
import com.suviridedriver.databinding.ActivityLanguageBinding
import com.suviridedriver.databinding.ActivityRidePaymentBinding
import com.suviridedriver.model.collect_payment.CollectPayment
import com.suviridedriver.ui.bottom_navigation.MainActivity
import com.suviridedriver.ui.language.LanguageAdapter
import com.suviridedriver.ui.language.LanguageViewModel
import com.suviridedriver.ui.rating.RatingActivity
import com.suviridedriver.utils.BaseActivity
import com.suviridedriver.utils.Constants
import com.suviridedriver.utils.NetworkResult

class RidePaymentActivity : BaseActivity() {
    private var name = ""
    private var fare = ""
    private lateinit var binding: ActivityRidePaymentBinding
    var context: Context = this
    var rideId = ""
    val ridePaymentViewModel by viewModels<RidePaymentViewModel>()
    //  var mIntent = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRidePaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent != null) {
            rideId = intent.getStringExtra("rideId")!!
             fare = intent.getStringExtra("fare").toString()
             name = intent.getStringExtra("name").toString()
            val date_time = intent.getStringExtra("date_time")
            val pickupLocation = intent.getStringExtra("pickupLocation")
            val destinationLocation = intent.getStringExtra("destinationLocation")
            binding.tvPayment.text = "₹ $fare"
            binding.tvDateTime.text = "₹ $date_time"
            binding.tvPickupAddress.text = pickupLocation
            binding.tvDropAddress.text = destinationLocation
        }

        /* binding.ivBack.setOnClickListener {
             finish()
         }*/

        binding.tvDone.setOnClickListener {
            if (rideId != null && !rideId.equals("")) {
                ridePaymentViewModel.collectPayment(CollectPayment(rideId))
            }
        }

        ridePaymentViewModel.collectPaymentResponseLiveData.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.d(Constants.TAG, it.data.toString())
                        showToast(it.data.message)
                        val mIntent = Intent(context, RatingActivity::class.java)
                        mIntent.putExtra("fare", fare)
                        mIntent.putExtra("name", name)
                        startActivity(mIntent)
                        finish()
                    }
                }
                is NetworkResult.Error -> {
                    Log.d(Constants.TAG, "Error - " + it.message)
                }
                is NetworkResult.Loading -> {
                    showLoader()
                    Log.d(Constants.TAG, "Loading")
                }
            }
        }
    }
}