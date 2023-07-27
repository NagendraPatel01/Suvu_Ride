package com.suviridedriver.ui.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity


class NetworkChangeReceiver : BroadcastReceiver() {

    //public static ReceiverListener Listener
    companion object{
        var newtWorkListner:NewtWorkListner? = null
        var isActivityCalled = false
    }
    override fun onReceive(context: Context, intent: Intent?) {
        try {
            if (isOnline(context)) {
              //  dialog(true)
                //Log.e("keshav", "Online Connect Intenet ")
                newtWorkListner!!.onNetworkAvailable(true)
            } else {
              //  dialog(false)
               // Log.e("keshav", "Conectivity Failure !!! ")
                if (!isActivityCalled){
                    isActivityCalled = true
                    context.startActivity(Intent(context, NetworkConnectionActivity::class.java))
                    val handler= Handler()
                    val commandCountRunnable:Runnable= object :Runnable{
                        override fun run() {
                            handler.removeCallbacks(this)
                            isActivityCalled=false
                        }

                    }
                    handler.postDelayed(commandCountRunnable,3000)
                }
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

     fun isOnline(context: Context): Boolean {
        val cm: ConnectivityManager =
            context.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo: NetworkInfo? = cm.getActiveNetworkInfo()
        //should check null because in airplane mode it will be null
        return netInfo != null && netInfo.isConnected()
    }

    interface NewtWorkListner{
        fun onNetworkAvailable(value:Boolean)
    }
}