package com.suviridecustomer.ui.number_verification

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.suviridedriver.R

class GenericTextWatcher internal constructor(
    private val currentView: EditText?,
    private val nextView: EditText?
) :
    TextWatcher {
    override fun afterTextChanged(editable: Editable) {
        val text = editable.toString()
        when (currentView!!.id) {
            R.id.etOtp1st -> if (text.length == 1) {
                nextView!!.requestFocus()
                nextView.setTextColor(Color.WHITE)
                nextView.hint = ""
                nextView.setBackgroundResource(R.drawable.after_otp_bg)
            }
            R.id.etOtp2nd -> if (text.length == 1) {
                nextView!!.requestFocus()
                nextView.setTextColor(Color.WHITE)
                nextView.hint = ""
                nextView.setBackgroundResource(R.drawable.after_otp_bg)
            }
            R.id.etOtp3rd -> if (text.length == 1) {
                nextView!!.requestFocus()
                nextView.setTextColor(Color.WHITE)
                nextView.hint = ""
                nextView.setBackgroundResource(R.drawable.after_otp_bg)
            }
            R.id.etOtp4th -> if (text.length == 1) {
                nextView!!.requestFocus()
                nextView.setTextColor(Color.WHITE)
                nextView.hint = ""
                nextView.setBackgroundResource(R.drawable.after_otp_bg)
            }
            R.id.etOtp5th -> if (text.length == 1) {
                nextView!!.requestFocus()
                nextView.setTextColor(Color.WHITE)
                nextView.hint = ""
                nextView.setBackgroundResource(R.drawable.after_otp_bg)
            }
            //You can use EditText3 same as above to hide the keyboard
        }
    }

    override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        // TODO Auto-generated method stub
    }

    override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        // TODO Auto-generated method stub
    }
}