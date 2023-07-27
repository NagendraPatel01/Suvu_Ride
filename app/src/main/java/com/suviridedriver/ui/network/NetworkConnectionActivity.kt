package com.suviridedriver.ui.network

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.suviridedriver.databinding.ActivityNetworkConnectionBinding
import com.suviridedriver.ui.splash.SplashActivity
import com.suviridedriver.utils.BaseActivity
import com.suviridedriver.utils.CommonFunction

class NetworkConnectionActivity : BaseActivity(),NetworkChangeReceiver.NewtWorkListner {

    lateinit var binding: ActivityNetworkConnectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNetworkConnectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivNoInternet.layoutParams.height = (CommonFunction.getScreenWidth()*0.50).toInt()
        binding.ivNoInternet.layoutParams.width = (CommonFunction.getScreenWidth()*0.50).toInt()

        NetworkChangeReceiver.newtWorkListner = this
        binding.tvRetry.setOnClickListener {
            if(isOnline(this@NetworkConnectionActivity)){
                finish()
            }

        }
    }

    fun isOnline(context: Context): Boolean {
        val cm: ConnectivityManager =
            context.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo: NetworkInfo? = cm.getActiveNetworkInfo()
        //should check null because in airplane mode it will be null
        return netInfo != null && netInfo.isConnected()
    }


    override fun onNetworkAvailable(value: Boolean) {
        finish()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }
}