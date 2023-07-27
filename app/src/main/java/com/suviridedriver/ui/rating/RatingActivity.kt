package com.suviridedriver.ui.rating

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.viewModels
import com.suviridedriver.R
import com.suviridedriver.databinding.ActivityRatingBinding
import com.suviridedriver.model.rating.RatingRequest
import com.suviridedriver.ui.bottom_navigation.MainActivity
import com.suviridedriver.ui.bottom_navigation.MainActivity.Companion.isWindowShow
import com.suviridedriver.utils.AppSession
import com.suviridedriver.utils.BaseActivity
import com.suviridedriver.utils.Constants
import com.suviridedriver.utils.Constants.TAG
import com.suviridedriver.utils.NetworkResult

class RatingActivity : BaseActivity() {
    private lateinit var binding: ActivityRatingBinding
    var context: Context = this
    var starArray: ArrayList<ImageView> = ArrayList()
    var starRate = 0
    var tag = ""
    private val ratingViewModel by viewModels<RatingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (intent != null) {
            val fare = intent.getStringExtra("fare")
            val name = intent.getStringExtra("name")
            binding.tvAmount.text = "₹ $fare"
            binding.tvDriverName.text = "How Was Your Ride With $name?"
        }

        binding.ivBack.setOnClickListener {
            isWindowShow = false
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        starArray.add(binding.star1)
        starArray.add(binding.star2)
        starArray.add(binding.star3)
        starArray.add(binding.star4)
        starArray.add(binding.star5)

        binding.star1.setOnClickListener {
            star(starArray, 0)
        }
        binding.star2.setOnClickListener {
            star(starArray, 1)
        }
        binding.star3.setOnClickListener {
            star(starArray, 2)
        }
        binding.star4.setOnClickListener {
            star(starArray, 3)
        }
        binding.star5.setOnClickListener {
            star(starArray, 4)
        }

        binding.text1.setOnClickListener {
            removeBack()
            binding.text1.setBackgroundResource(R.drawable.edit_back_1)
            tag = binding.text1.text.toString()
            binding.text1.setTextColor(getColor(R.color.black))
        }
        binding.text2.setOnClickListener {
            removeBack()
            binding.text2.setBackgroundResource(R.drawable.edit_back_1)
            tag = binding.text2.text.toString()
            binding.text2.setTextColor(getColor(R.color.black))
        }
        binding.text3.setOnClickListener {
            removeBack()
            binding.text3.setBackgroundResource(R.drawable.edit_back_1)
            tag = binding.text3.text.toString()
            binding.text3.setTextColor(getColor(R.color.black))
        }
        binding.text4.setOnClickListener {
            removeBack()
            binding.text4.setBackgroundResource(R.drawable.edit_back_1)
            tag = binding.text4.text.toString()
            binding.text4.setTextColor(getColor(R.color.black))
        }

        binding.submit.setOnClickListener {
            if (starRate != 0) {
                val rideID = AppSession.getValue(context, Constants.RIDE_ID)
                ratingViewModel.submitRating(
                    RatingRequest(
                        starRate,
                        binding.edtTellMore.text.toString(),
                        tag,
                        rideID!!
                    )
                )

            } else {
                showToast("Please give rating.")
            }
        }

        ratingViewModel.submitRatingResponseLiveData.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.d(TAG, it.data.toString())
                        showToast(it.data.message)
                        if (it.data.success){
                            isWindowShow = false
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    }
                }
                is NetworkResult.Error -> {
                    Log.d(TAG, "Error" + it.message)
                }
                is NetworkResult.Loading -> {
                    showLoader()
                    Log.d(TAG, "Loading")
                }
            }
        }
    }

    fun removeBack() {
        binding.text1.setBackgroundResource(R.drawable.edit_back)
        binding.text2.setBackgroundResource(R.drawable.edit_back)
        binding.text3.setBackgroundResource(R.drawable.edit_back)
        binding.text4.setBackgroundResource(R.drawable.edit_back)
    }

    fun star(imageViews: ArrayList<ImageView>, j: Int) {
        starRate = j + 1
        for (img in imageViews) {
            img.setImageResource(R.drawable.ratings_star_img)
        }
        if (j < 4) {
            for (i in j + 1 until imageViews.toArray().size) {
                imageViews[i].setImageResource(R.drawable.rating_star_img_fade)
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}