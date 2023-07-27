package com.suviridedriver.ui.make_payment.network;

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class APIClient {
    companion object {

        private var retofit: Retrofit? = null

        val refereshToken: Retrofit
            get() {
                retofit = Retrofit.Builder()
                    .baseUrl("https://test.cashfree.com/api/v2/")
                    .client(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                return retofit!!
            }

        val identityService: Retrofit
            get() {

                retofit = Retrofit.Builder()
                    .baseUrl("https://test.cashfree.com/api/v2/")
                    .client(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                return retofit!!
            }


        private fun getOkHttpClient(timeout: Long = 90): OkHttpClient {
            val logging = HttpLoggingInterceptor()
            logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }

            val httpClient = OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .addInterceptor(logging)
            httpClient.addInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val original: Request = chain.request()

                  //  val prefs = PreferenceHelper.defaultPrefs(App.applicationContext())
                    // Request customization: add request headers

                    val requestBuilder: Request.Builder = original.newBuilder()
                        .addHeader("x-client-id", "TEST370160395206780c433649d46c061073")
                        .addHeader("x-client-secret", "TEST76070e32bef4562fb72e4b8bb077602b58e002d4")

                   /* prefs[KEY_AUTH_TOKEN, ""]?.let { key ->
                        //Log.e("Bearer","---: "+ key)
                        requestBuilder.addHeader("Authorization", "Zoho-oauthtoken  $key")
                        //requestBuilder.addHeader("Authorization", "$key")
                        //requestBuilder.addHeader("Authorization", "Bearer $key")
                    }*/
                   /* try {
                        prefs[KEY_CONCURRENCY_STAMP, ""]?.let { key ->
                            requestBuilder.addHeader("x-coreplatform-concurrencystamp", "$key")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }*/

                    val request: Request = requestBuilder.build()
                    return chain.proceed(request)
                }
            })

            return httpClient.build()
        }
    }
}