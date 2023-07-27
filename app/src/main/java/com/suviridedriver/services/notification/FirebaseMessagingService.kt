package com.suviridedriver.services.notification

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.suviridedriver.R
import com.suviridedriver.ui.bottom_navigation.MainActivity
import com.suviridedriver.ui.bottom_navigation.MainActivity.Companion.mPlayer
import com.suviridedriver.utils.AppSession
import com.suviridedriver.utils.Constants
import com.suviridedriver.utils.Constants.NOTIFICATION_ID
import okhttp3.OkHttpClient
import java.net.HttpURLConnection
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class FirebaseMessagingService : FirebaseMessagingService() {
    val TAG = "FirebaseMessagingService"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO(developer): Handle FCM messages here.
        val data = remoteMessage.data
        Log.i(TAG, "data - $data")
        Log.i(TAG, "data - messageReceived")
        if (isAppInforegrounded()) {
            sendBroadcast(data)
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "onNewToken Refreshed token : $token")
        Log.d(TAG, "onNewToken Refreshed token : newtoken")
       /* val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        preferences.edit().putString(Constants.NEW_FCM_TOKEN, token).apply()*/
        AppSession.save(this, Constants.NEW_FCM_TOKEN, token)
    }

    override fun handleIntent(intent: Intent?) {
        // super.handleIntent(intent)
        Log.d(TAG, "handleIntent : $intent")
        Log.d(TAG, "handleIntent : handleIntent")
        val bundle = intent?.extras
        if (bundle != null) {
            for (key in intent.extras!!.keySet()) {
                val value = intent.extras!![key]
                Log.i(TAG, "Key: $key Value: $value")
            }
        }
        if (isAppInforegrounded() && intent?.getStringExtra("gcm.notification.title") != null) {
            sendLocalBroadcast(intent!!)
        }
        /* else {
             Log.d(TAG, "handleIntent : background $intent")
             sendActivity(intent!!)
         }*/
        if (intent?.getStringExtra("gcm.notification.title") != null) {
            sendPushNotification(intent!!)
        }

        Log.d(
            TAG,
            "handleIntent : notification " + intent?.getStringExtra("gcm.notification.title")
        )
        Log.d(TAG, "handleIntent : notification " + intent?.getStringExtra("gcm.notification.body"))
        Log.d(TAG, "handleIntent : notification " + intent?.getStringExtra("customer_name"))
        Log.d(TAG, "handleIntent : notification " + intent?.getStringExtra("ride_id"))
    }

    fun sendBroadcast(data: Map<String, String>) {

        try {
            val intent = Intent("your_action")
            intent.putExtra("customer_name", data.get("customer_name"))
            intent.putExtra("customer_id", data.get("customer_id"))
            intent.putExtra("status", data.get("status"))
            intent.putExtra("pickupLocation", data.get("pickupLocation"))
            intent.putExtra("pickupLongitude", data.get("pickupLongitude"))
            intent.putExtra("fare", data.get("fare"))
            intent.putExtra("pickupLatitude", data.get("pickupLatitude"))
            intent.putExtra("destinationLocation", data.get("destinationLocation"))
            intent.putExtra("ride_id", data.get("ride_id"))
            intent.putExtra("numberOfPassengers", data.get("numberOfPassengers"))
            intent.putExtra("destinationLongitude", data.get("destinationLongitude"))
            intent.putExtra("destinationLatitude", data.get("destinationLatitude"))
            intent.putExtra("finalFare", data.get("finalFare"))
            intent.putExtra("discount", data.get("discount"))
            intent.putExtra("crnnumber", data.get("crnnumber"))
            intent.putExtra("scheduledTime", data.get("scheduledTime"))
            intent.putExtra("scheduledDate", data.get("scheduledDate"))
            intent.putExtra("scheduled", data.get("scheduled"))
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        } catch(e: Exception) {
            Log.d(TAG, "sendBroadcast: "+e.message)
        }

    }

    fun sendLocalBroadcast(intent: Intent) {
        val intent1 = Intent("your_action")
        intent1.putExtras(intent.extras!!)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent1)
    }


    private fun sendActivity(intent: Intent) {
        Log.d(TAG, "handleIntent : sendActivity $intent")
        val intent1 = Intent(this, MainActivity::class.java)
        intent1.putExtras(intent.extras!!)
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        /* intent.putExtra("customer_id", data.get("customer_id"))
         intent.putExtra("status", data.get("status"))
         intent.putExtra("pickupLocation", data.get("pickupLocation"))
         intent.putExtra("pickupLongitude", data.get("pickupLongitude"))
         intent.putExtra("fare", data.get("fare"))
         intent.putExtra("pickupLatitude", data.get("pickupLatitude"))
         intent.putExtra("destinationLocation", data.get("destinationLocation"))
         intent.putExtra("ride_id", data.get("ride_id"))
         intent.putExtra("numberOfPassengers", data.get("numberOfPassengers"))
         intent.putExtra("destinationLongitude", data.get("destinationLongitude"))
         intent.putExtra("destinationLatitude", data.get("destinationLatitude"))*/


        startActivity(intent1)
    }

    fun isAppInforegrounded(): Boolean {
        val appProcessInfo = RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND ||
                appProcessInfo.importance == RunningAppProcessInfo.IMPORTANCE_VISIBLE)
    }

    private fun isAppIsInBackground(context: Context): Boolean {
        var isInBackground = true
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            val runningProcesses = am.runningAppProcesses
            for (processInfo in runningProcesses) {
                if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (activeProcess in processInfo.pkgList) {
                        if (activeProcess == context.getPackageName()) {
                            isInBackground = false
                        }
                    }
                }
            }
        } else {
            val taskInfo = am.getRunningTasks(1)
            val componentInfo = taskInfo[0].topActivity
            if (componentInfo!!.packageName == context.getPackageName()) {
                isInBackground = false
            }
        }
        return isInBackground
    }

    @SuppressLint("WrongConstant")
    private fun sendNotification(messageBody: RemoteMessage) {
        try {
            var intent: Intent? = null
            intent = Intent(this, MainActivity::class.java)



            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            var pendingIntent: PendingIntent? = null
            pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
            } else {
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            }
            val CHANNEL_ID = "my_channel_01"
            val mBuilder = NotificationCompat.Builder(this)
            val bitmap = getBitmapFromUrl(messageBody.data.get("customer_id"))
            val notification =
                mBuilder.setSmallIcon(getNotificationIcon(mBuilder)).setTicker("LickR").setWhen(0)
                    .setAutoCancel(true)
                    .setSmallIcon(getNotificationIcon(mBuilder))
                    .setLargeIcon(bitmap)
                    .setContentTitle(messageBody.notification!!.title)
                    .setStyle(
                        NotificationCompat.BigPictureStyle()
                            .setSummaryText(messageBody.data["message"]) // can be null
                            .bigPicture(bitmap)
                    )/*Notification with Image*/
                    .setContentText(messageBody.data["message"])
                    .setContentIntent(pendingIntent)
                    //  .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setChannelId(CHANNEL_ID)


            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val time = System.currentTimeMillis().toInt()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // The id of the channel.
                val name: CharSequence = "Remedy" // The user-visible name of the channel.
                val importance = NotificationManager.IMPORTANCE_HIGH
                val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
                notificationManager.createNotificationChannel(mChannel)
            }
            notificationManager.notify(time, notification.build())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getNotificationIcon(notificationBuilder: NotificationCompat.Builder): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val color = 0x008000
            notificationBuilder.color = color
            return R.drawable.logo
        }
        return R.drawable.logo
    }

    private fun getBitmapFromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    fun OkHttpClient.Builder.ignoreAllSSLErrors(): OkHttpClient.Builder {
        val naiveTrustManager = object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) = Unit
        }

        val insecureSocketFactory = SSLContext.getInstance("TLSv1.2").apply {
            val trustAllCerts = arrayOf<TrustManager>(naiveTrustManager)
            init(null, trustAllCerts, SecureRandom())
        }.socketFactory

        sslSocketFactory(insecureSocketFactory, naiveTrustManager)
        hostnameVerifier(HostnameVerifier { _, _ -> true })
        return this
    }

    companion object {
        private const val NOTIFICATION_ID_EXTRA = "notificationId"
        private const val IMAGE_URL_EXTRA = "imageUrl"
        private const val ADMIN_CHANNEL_ID = "admin_channel"
    }

    fun sendPushNotification(intent: Intent) {

        //Lokesh
       try {
           var notificationManager: NotificationManager
           var mBuilder: NotificationCompat.Builder? = null
           val channelId = "com.suviridedriver2"
           val descr = "My notification2"


           notificationManager =
               getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


           val intent1 = Intent(this, MainActivity::class.java)
           intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
           intent1.putExtras(intent.extras!!)
           val pendingIntent =
               PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_IMMUTABLE)

           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               val importance = NotificationManager.IMPORTANCE_LOW
               val notificationChannel = NotificationChannel(channelId, descr, importance)
               notificationChannel.enableLights(true)
               notificationChannel.enableVibration(true)
               notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
               notificationChannel.vibrationPattern =
                   longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
               assert(notificationManager != null)
               notificationManager.createNotificationChannel(notificationChannel)
               mBuilder = buildNotification(
                   this,
                   intent?.getStringExtra("gcm.notification.body"),
                   intent?.getStringExtra("gcm.notification.title"),
                   pendingIntent,
                   R.drawable.logo,
                   channelId
               )
               mBuilder!!.setChannelId(channelId)
           } else {
                mBuilder = NotificationCompat.Builder(this, channelId)
                mBuilder.setSmallIcon(R.drawable.logo)
                mBuilder.setContentTitle( intent?.getStringExtra("gcm.notification.title"))
                    .setContentText(intent?.getStringExtra("gcm.notification.body"))
                    .setAutoCancel(false)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            }


               mPlayer = MediaPlayer.create(this, R.raw.notification)
               if (mPlayer != null)
                   mPlayer!!.start()

           if (notificationManager != null && mBuilder!= null) {
               notificationManager.notify(NOTIFICATION_ID, mBuilder!!.build())
           }


       }
       catch (e:Exception)
       {
           e.printStackTrace()
       }
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun buildNotification(
        mContext: Context,
        text: String?,
        fromnumber: String?,
        pendingIntent: PendingIntent?,
        icon: Int,
        channelId: String?
    ): NotificationCompat.Builder? {

        Log.d(TAG, "buildNotification: alarm")
        return NotificationCompat.Builder(mContext, channelId!!)
            .setSound(null)
            .setSmallIcon(icon)
            .setContentTitle(fromnumber)
            .setSubText(mContext.getString(R.string.app_name))
            .setContentText(text)
            .setAutoCancel(true)
            //.setOngoing(true)
            // making the notification clickable
            .setContentIntent(pendingIntent)

    }

}