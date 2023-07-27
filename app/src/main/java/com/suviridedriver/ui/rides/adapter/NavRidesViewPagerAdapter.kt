package com.suviridedriver.ui.rides.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.suviridedriver.model.rides.Ride
import com.suviridedriver.ui.rides.fragments.CancelledFragment
import com.suviridedriver.ui.rides.fragments.CompletedFragment
import com.suviridedriver.ui.rides.fragments.OngoingFragment

class NavRidesViewPagerAdapter(childFragmentManager: FragmentManager,val totalRides: List<Ride>) : FragmentPagerAdapter(childFragmentManager) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> OngoingFragment(totalRides.get(1).data)
            1 -> CompletedFragment(totalRides.get(0).data)
            else -> CancelledFragment(totalRides.get(2).data)
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Ongoing"
            1 -> "Completed"
            else -> "Cancelled"
        }
    }
}