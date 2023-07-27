package com.suviridedriver.ui.rides.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.suviridedriver.R
import com.suviridedriver.model.rides.Data
import com.suviridedriver.ui.rides.adapter.CancelledAdpater

class CancelledFragment( val list: List<Data>) : Fragment() {
    var recyclerView: RecyclerView? = null
    var tvMSG: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_completed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.rvOnGoing)
        tvMSG = view.findViewById(R.id.tvMSGComplete)
    }

    override fun onResume() {
        super.onResume()
        if (list.size != 0){
            val onGoingAdpater = CancelledAdpater(requireContext(),list)
            recyclerView!!.layoutManager = LinearLayoutManager(context)
            recyclerView!!.adapter = onGoingAdpater
        }else{
            tvMSG!!.text = "No Cancelled Ride"
            tvMSG!!.visibility = View.VISIBLE
        }
    }
}