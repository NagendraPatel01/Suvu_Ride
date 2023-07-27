package com.suviridedriver.api

import android.util.Log
import com.suviridedriver.utils.AppSession
import com.suviridedriver.utils.Constants
import com.suviridedriver.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.security.cert.CertPathValidatorException
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {

    @Inject
    lateinit var tokenManager: TokenManager

    @Throws(IOException::class,NullPointerException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var proceed: Response? = null
        val original: Request = chain.request()
        val requestBuilder: Request.Builder =
            original.newBuilder().addHeader("Content-Type", "application/json")
        val token = ""//TokenManager.getToken()
       // Log.d("SplashActivityLog", "AccessToken - auth " + tokenManager.getToken())
        if (token != null) {
            Log.i("token_a","Token - $token")
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        try {
            val request: Request = requestBuilder.build()
            proceed = chain.proceed(request)
        } catch (e: CertPathValidatorException) {
            e.printStackTrace()
            Log.i("proceed","proceed error - "+e.message)
        }
        catch (e:Exception)
        {
            e.printStackTrace()
            Log.i("proceed","proceed error - "+e.message)
        }
        return proceed!!
    }
}