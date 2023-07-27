package com.suviridedriver.ui.help_support

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.suviridedriver.R
import com.suviridedriver.utils.BaseActivity

class SupportActivity : BaseActivity() {

    private val otherIssue: LinearLayout get() = findViewById(R.id.llOtherIssue)
    private val support: LinearLayout get() = findViewById(R.id.llSupport)
    private val ivBack: ImageView get() = findViewById(R.id.ivBack)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)

        ivBack.setOnClickListener {
            finish()
        }

        support.setOnClickListener {
            showDialog()
        }

        otherIssue.setOnClickListener {
           startActivity(Intent(this, OtherIssueActivity::class.java))
        }
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.contact_support_bottom_sheet_layout)

        val call = dialog.findViewById<TextView>(R.id.tvCall)
        val cancel = dialog.findViewById<TextView>(R.id.tvCancel)

        call.setOnClickListener {
            makeCall("8878117942")
        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
    }
}