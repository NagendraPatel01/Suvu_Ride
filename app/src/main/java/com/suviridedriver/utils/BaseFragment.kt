package com.suviridedriver.utils

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    private var progressDialog: ProgressDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("base_fragment","life_of_fragment - onViewCreated")
        progressDialog = ProgressDialog(requireContext())
    }

    fun showLoader() {
        try {
            progressDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideLoader() {
        try {
            if (null != progressDialog) progressDialog!!.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}