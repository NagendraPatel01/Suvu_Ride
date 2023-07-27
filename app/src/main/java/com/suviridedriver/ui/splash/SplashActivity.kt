package com.suviridedriver.ui.splash

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.suviridecustomer.model.screenLabel.ScreenLabelRequest
import com.suviridedriver.BuildConfig
import com.suviridedriver.R
import com.suviridedriver.databinding.ActivitySplashBinding
import com.suviridedriver.ui.language.LanguageActivity
import com.suviridedriver.utils.AppSession
import com.suviridedriver.utils.BaseActivity
import com.suviridedriver.utils.Constants
import com.suviridedriver.utils.Constants.TAG
import com.suviridedriver.utils.FlowManager


class SplashActivity : BaseActivity() {
    lateinit var binding: ActivitySplashBinding
    lateinit var versionCode: String
    var context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)

        showLog("screen_type","Screen - "+AppSession.getValue(context, Constants.SCREEN_TYPE))

       // Glide.with(this).load(R.drawable.loading_gif).into(binding.imageView)
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(binding.root)


        // Set version name
        versionCode = BuildConfig.VERSION_NAME
        showLog(TAG, "version $versionCode")

        // get screen labels
        if (AppSession.getValue(context, Constants.LANGUAGE_ID) != null) {
            val screenLabelRequest = ScreenLabelRequest(
                "driverScreens",
                AppSession.getValue(context, Constants.LANGUAGE_ID)!!
            )
            getScreenLabel(screenLabelRequest)
        }

        if (AppSession.getValue(context, Constants.FCM_TOKEN) == null) {
            getFCMToken()
        } else {
            showLog("SplashActivityLog", "FCM - " + AppSession.getValue(context, Constants.FCM_TOKEN))
        }

        Handler().postDelayed({
            if (AppSession.getValue(context, Constants.SCREEN_TYPE) != null) {
                FlowManager().navigate(context, AppSession.getValue(context, Constants.SCREEN_TYPE))

            } else {
                startActivity(Intent(context, LanguageActivity::class.java))
                finish()
            }
        }, 3000)
    }

    // for get fcm token from firebase
    fun getFCMToken() {
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Get new FCM registration token
                    val token = task.result
                    AppSession.save(context, Constants.FCM_TOKEN, token)
                    showLog(TAG, "FCM Token - $token")
                }
                if (task.isCanceled) {
                    Log.i(TAG, "FCM Token isCanceled - ${task.exception}")
                    return@OnCompleteListener
                }
                if (task.isComplete) {
                    Log.i(TAG, "FCM Token isComplete - ${task.exception}")
                    return@OnCompleteListener
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}