package com.suviridedriver.ui.persnal_details

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.suviridedriver.databinding.ActivityPersonalDetailsBinding
import com.suviridedriver.model.state_city.AddStateCityRequest
import com.suviridedriver.model.state_city.City
import com.suviridedriver.model.state_city.State
import com.suviridedriver.repository.PersonalDetailsRepository
import com.suviridedriver.ui.bottom_navigation.MainViewModel
import com.suviridedriver.utils.AppSession
import com.suviridedriver.utils.BaseActivity
import com.suviridedriver.utils.Constants
import com.suviridedriver.utils.Constants.TAG
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class PersonalDetailsActivity : BaseActivity() {
    private var stateList: List<State>? = null
    private var cityList: List<City>? = null
    private lateinit var binding: ActivityPersonalDetailsBinding
    val personalDetailsViewModel by viewModels<PersonalDetailsViewModel>()
    val context: Context = this
    var stateId = ""
    var cityId = ""
    var stateName = ""
    var cityName = ""
    val stateArray: MutableList<String> = ArrayList()
    val cityArray: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        stateArray.add("Select State")
        cityArray.add("Select City")
        personalDetailsViewModel.getState()
        setObserves()

        binding.ivBack.setOnClickListener {
            finish()
        }

        // set adapter and data
        // Drop down layout style - list view with radio button
        val stateAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, stateArray)
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerState.setAdapter(stateAdapter)

        // Spinner click listener
        binding.spinnerState.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                (parent.getChildAt(0) as TextView).setTextSize(12f)
                stateName = (parent.getChildAt(0) as TextView).text.toString()
                // showToast(state.toString() + " - $position")
                stateId = ""
                if (stateList != null && position != 0) {
                    val idIndex = position - 1
                    stateId = stateList!!.get(idIndex)._id
                    personalDetailsViewModel.getCities(stateId)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        // set adapter and data
        // Drop down layout style - list view with radio button
        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cityArray)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCity.setAdapter(cityAdapter)

        // Spinner click listener
        binding.spinnerCity.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                (parent.getChildAt(0) as TextView).setTextSize(12f)
                cityName = (parent.getChildAt(0) as TextView).text.toString()
                //showToast(cityName.toString() + " - $position")
                cityId = ""
                if (cityList != null && position != 0) {
                    val idIndex = position - 1
                    cityId = cityList!!.get(idIndex)._id
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        binding.tvSubmit.setOnClickListener {
            val validation = personalDetailsViewModel.validateCredentials(
                binding.edtHouseNumber.text.toString().trim(),
                binding.edtStreetName.text.toString().trim(),
                stateId,
                cityId,
                binding.edtAreaPincode.text.toString().trim()
            )

            val fullAddress = "${
                binding.edtHouseNumber.text.toString().trim()
            }, ${
                binding.edtStreetName.text.toString().trim()
            }, $stateName, $cityName, Pin Code - ${binding.edtAreaPincode.text.toString().trim()}"

            if (validation.first) {
                personalDetailsViewModel.updateStateCity(
                    AddStateCityRequest(
                        cityId,
                        stateId,
                        fullAddress
                    )
                )
            } else {
                showToast(validation.second)
            }
        }

    }

    private fun setObserves() {
        try {
            personalDetailsViewModel.stateResponseResponse.observe(this) {
                hideLoader()
                when (it) {
                    is NetworkResult.Success -> {
                        if (it.data != null) {
                            showLog(TAG, it.data.toString())
                            try {
                                stateList = it.data.data
                                if (stateList != null && stateList!!.size != 0) {
                                    for (state in stateList!!) {
                                        stateArray.add(state.name)
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
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

            personalDetailsViewModel.cityResponse.observe(this) {
                hideLoader()
                when (it) {
                    is NetworkResult.Success -> {
                        if (it.data != null) {
                            showLog(TAG, it.data.toString())
                            try {
                                cityList = it.data.data
                                if (cityList != null && cityList!!.size != 0) {
                                    for (city in cityList!!) {
                                        cityArray.add(city.name)
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
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

            personalDetailsViewModel.addStateCityResponse.observe(this) {
                hideLoader()
                when (it) {
                    is NetworkResult.Success -> {
                        if (it.data != null) {
                            showLog(TAG, it.data.toString())
                            showToast(it.data.message)
                            AppSession.save(context,Constants.FILED_TYPE,"make_payment")
                            finish()
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

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}