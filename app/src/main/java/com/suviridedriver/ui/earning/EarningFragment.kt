package com.suviridedriver.ui.earning

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.suviridedriver.databinding.FragmentEarningBinding
import com.suviridedriver.ui.earning.adapter.EarningAdapter
import com.suviridedriver.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EarningFragment : BaseFragment() {

    private var _binding: FragmentEarningBinding? = null
    private val earningViewModel by viewModels<EarningViewModel>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEarningBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        earningViewModel.getRidesResponse.observe(viewLifecycleOwner) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.i("Earning", "Success" + it.data)
                        binding.tvRidesEarningToday.text =
                            "${it.data.todayRidesCount}/₹${it.data.todayEarning}/-"
                        binding.tvRidesEarningMonthly.text =
                            "${it.data.thisMonthRideCount}/₹${it.data.thisMonthEarning}/-"
                        binding.tvRidesEarningLifeTime.text =
                            "${it.data.totalRidesCount}/₹${it.data.totalEarning}/-"

                        if (it.data.totalRides != null && it.data.totalRides.size > 0) {
                            val adapter = EarningAdapter(requireContext(), it.data.totalRides)
                            val linearLayoutManager = LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                            binding.rvEarnings.layoutManager = linearLayoutManager
                            binding.rvEarnings.adapter = adapter
                            binding.rlMain.visibility = View.VISIBLE
                        } else {
                            binding.tvMSGEarning.text = it.data.message
                            binding.tvMSGEarning.visibility = View.VISIBLE
                        }
                    }
                }
                is NetworkResult.Error -> {
                    Log.i("Earning", "Error" + it.message)
                    binding.tvMSGEarning.text = it.message
                    binding.tvMSGEarning.visibility = View.VISIBLE

                }
                is NetworkResult.Loading -> {
                    showLoader()
                    Log.i("Earning", "Loading")
                }
                else -> {}
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getEarning()
    }


    // for get earnings
    private fun getEarning() {
        earningViewModel.getEarning()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}