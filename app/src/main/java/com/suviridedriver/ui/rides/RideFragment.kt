package com.suviridedriver.ui.rides

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.suviridedriver.databinding.FragmentRidesBinding
import com.suviridedriver.ui.rides.adapter.NavRidesViewPagerAdapter
import com.suviridedriver.utils.BaseFragment
import com.suviridedriver.utils.Constants
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RideFragment : BaseFragment() {

    private var _binding: FragmentRidesBinding? = null
    private val rideViewModel by viewModels<RideViewModel>()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // val rideViewModel = ViewModelProvider(this).get(RideViewModel::class.java)

        _binding = FragmentRidesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        getRidesObserver()

        return root
    }

    override fun onResume() {
        super.onResume()
        rideViewModel.getRides()
    }

    // for get all rides
    private fun getRidesObserver() {

        rideViewModel.getRidesResponse.observe(requireActivity()) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    Log.d("rideFragment", "Success" + it.data)
                    if (it.data!!.rides != null) {
                        val adapter = NavRidesViewPagerAdapter(childFragmentManager, it.data.rides)
                        binding.viewPager.adapter = adapter
                        binding.tab.setupWithViewPager(binding.viewPager)
                    }else{

                    }
                }
                is NetworkResult.Error -> {
                    Log.i("rideFragment", "Error" + it.message)

                }
                is NetworkResult.Loading -> {
                    showLoader()
                    Log.i("rideFragment", "Loading")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}