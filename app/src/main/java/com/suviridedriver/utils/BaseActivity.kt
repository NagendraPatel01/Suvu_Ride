package com.suviridedriver.utils

import android.app.ActivityManager
import android.app.DatePickerDialog
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.suviridecustomer.model.screenLabel.ScreenLabelRequest
import com.suviridecustomer.model.screenLabel.ScreenLabelResponse
import com.suviridedriver.R
import com.suviridedriver.ui.network.NetworkChangeReceiver
import com.suviridedriver.ui.splash.ScreenLabelViewModel
import com.suviridedriver.utils.Constants.NOTIFICATION_ID
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.regex.Pattern

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {
    private var progressDialog: ProgressDialog? = null
    var mDecorView: View? = null
    var myBaseActivityContext: Context? = this
    var mReceiver: BroadcastReceiver? = null
    var baseRideStrat = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressDialog = ProgressDialog(this@BaseActivity)

        val filter = IntentFilter()
        mReceiver = NetworkChangeReceiver()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(mReceiver, filter)

        getScreenLabelObserver()
    }

     fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun hideLoader() {
        try {
            if (null != progressDialog) progressDialog!!.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun <T> convertString(data: String, myObject: T): T {
        val gson = Gson()
        return gson.fromJson(data, myObject!!::class.java)
    }

    fun showLoader() {
        try {
            progressDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelNotification()
    {
        try{
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(NOTIFICATION_ID)
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    fun showLog(tag: String?, msg: String?) {
        Log.d(tag, msg!!)
    }

    fun showDailogForError(msg: String?) {}
    open fun hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        mDecorView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE)
    }

    companion object {
        fun isServiceRunning(mContext: Context, serviceClass: Class<*>): Boolean {
            val manager = mContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name == service.service.className && service.pid != 0) {
                    //ShowLog("", "ser name "+serviceClass.getName());
                    return true
                }
            }
            return false
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        myBaseActivityContext = null
        // unregisterReceiver(mReceiver)
    }

    val screenLabelViewModel by viewModels<ScreenLabelViewModel>()
    fun getScreenLabel(screenLabelRequest: ScreenLabelRequest) {
        screenLabelViewModel.getScreenLabel(screenLabelRequest)

    }
    fun getScreenLabelObserver()
    {
        screenLabelViewModel.getScreenLabelResponseLiveData.observe(this) {
          //  hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.d(Constants.TAG, it.data.toString())
                        val json = Gson().toJson(it.data)
                        AppSession.save(myBaseActivityContext!!, Constants.ALL_SCREEN_DATA, json)
                    }
                }
                is NetworkResult.Error -> {
                    Log.d(Constants.TAG, "Error - " + it.message)
                }
                is NetworkResult.Loading -> {
                 //   showLoader()
                    Log.d(Constants.TAG, "Loading")
                }
            }
        }
    }

    open fun setScreenLabelData(screenType:String,label:String,textView:TextView){
        val json = AppSession.getValue(myBaseActivityContext!!, Constants.ALL_SCREEN_DATA)
        if(json != null){
            val gson = Gson()
            val allScreenDTO: ScreenLabelResponse = gson.fromJson(json, ScreenLabelResponse::class.java)

            for(i in 0 until allScreenDTO.data.size){
                if(allScreenDTO.data[i].screen_name == screenType){
                    for(j in 0 until allScreenDTO.data[i].doc.size){
                        if(allScreenDTO.data[i].doc[j].label_code == label){
                            textView.text = allScreenDTO.data[i].doc[j].label_text
                        }else{
                          //  showToast(""+allScreenDTO.data[i].doc[j].label_code)
                        }
                    }
                }
            }
        }
    }

    // set date in txt view
    fun showDate(textView: TextView):String{
        hideKeyBoard1(textView as View,this)
        var sDate = ""
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
//            { view, year, monthOfYear, dayOfMonth -> date_txt.text = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year) },
            { view, year, monthOfYear, dayOfMonth ->
                sDate = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year.toString())
                textView.setTextColor(getColor(R.color.white))
                textView.text = sDate
            },
            year, month, day
        )

        // datePickerDialog.updateDate(dateArray[2].toInt(), dateArray[1].toInt()-1, dateArray[0].toInt())
        datePickerDialog.datePicker.maxDate = c.getTimeInMillis();

        datePickerDialog.show()

        return sDate
    }
    fun showValidityDate(textView: TextView):String{
        hideKeyBoard1(textView as View,this)
        var sDate = ""
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
//            { view, year, monthOfYear, dayOfMonth -> date_txt.text = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year) },
            { view, year, monthOfYear, dayOfMonth ->
                sDate = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year.toString())
                textView.setTextColor(getColor(R.color.white))
                textView.text = sDate
            },
            year, month, day
        )

        // datePickerDialog.updateDate(dateArray[2].toInt(), dateArray[1].toInt()-1, dateArray[0].toInt())
        datePickerDialog.datePicker.minDate = c.getTimeInMillis();


        datePickerDialog.show()

        return sDate
    }

    open fun getEditTextFilterName(): InputFilter? {
        return object : InputFilter {
            override fun filter(
                source: CharSequence,
                start: Int,
                end: Int,
                dest: Spanned,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                var keepOriginal = true
                val sb = StringBuilder(end - start)
                for (i in start until end) {
                    val c = source[i]
                    if (isCharAllowed(c)) // put your condition here
                        sb.append(c) else keepOriginal = false
                }
                return if (keepOriginal) null else {
                    if (source is Spanned) {
                        val sp = SpannableString(sb)
                        TextUtils.copySpansFrom(source as Spanned, start, sb.length, null, sp, 0)
                        sp
                    } else {
                        sb
                    }
                }
            }

            private fun isCharAllowed(c: Char): Boolean {
                val ps = Pattern.compile("^[a-zA-Z \\u0900-\\u097F\\s]+$")
                val ms = ps.matcher(c.toString())
                return ms.matches()
            }
        }
    }

    open fun getEditTextFilterAddress(): InputFilter? {
        return object : InputFilter {
            override fun filter(
                source: CharSequence,
                start: Int,
                end: Int,
                dest: Spanned,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                var keepOriginal = true
                val sb = StringBuilder(end - start)
                for (i in start until end) {
                    val c = source[i]
                    if (isCharAllowed(c)) // put your condition here
                        sb.append(c) else keepOriginal = false
                }
                return if (keepOriginal) null else {
                    if (source is Spanned) {
                        val sp = SpannableString(sb)
                        TextUtils.copySpansFrom(source as Spanned, start, sb.length, null, sp, 0)
                        sp
                    } else {
                        sb
                    }
                }
            }

            private fun isCharAllowed(c: Char): Boolean {
                val ps = Pattern.compile("^[a-zA-Z0123456789, \\u0900-\\u097F\\s]+$")
                val ms = ps.matcher(c.toString())
                return ms.matches()
            }
        }
    }

    open fun getEditTextFilterLicence(): InputFilter? {
        return object : InputFilter {
            override fun filter(
                source: CharSequence,
                start: Int,
                end: Int,
                dest: Spanned,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                var keepOriginal = true
                val sb = StringBuilder(end - start)
                for (i in start until end) {
                    val c = source[i]

                    if (isCharAllowed(c)) // put your condition here
                        sb.append(c) else keepOriginal = false
                }
                return if (keepOriginal) null else {
                    if (source is Spanned) {
                        val sp = SpannableString(sb)
                        TextUtils.copySpansFrom(source as Spanned, start, sb.length, null, sp, 0)
                        showLog("licenceFilter","sp"+sp.toString().uppercase())
                        sp.toString().uppercase()

                    } else {
                        showLog("licenceFilter","sb"+sb.toString().uppercase())
                        sb
                    }
                }
            }

            private fun isCharAllowed(c: Char): Boolean {
                val ps = Pattern.compile("^[a-zA-Z0123456789]+$")
                val ms = ps.matcher(c.toString())
                return ms.matches()
            }


        }
    }
    open fun showKeyboard(editText: EditText) {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }
    open fun hideKeyBoard1(view: View, context: Context) {
        val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

     fun makeCall(number: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel: $number")
        startActivity(intent)
    }
}