package com.suviridecustomer.ui.number_verification

import android.annotation.SuppressLint
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import com.suviridedriver.R

class GenericKeyEvent internal constructor(
    private val currentView: EditText, private val previousView: EditText?
) : View.OnKeyListener {
    @SuppressLint("ResourceAsColor")
    override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.etOtp1st && currentView.text.isEmpty()) {
            //If current is empty then previous EditText's number will also be deleted
            currentView.hint = ""
            currentView.setBackgroundResource(R.drawable.before_otp_bg)
            previousView!!.text = null
            previousView.requestFocus()
            return true
        }
        return false
    }
}