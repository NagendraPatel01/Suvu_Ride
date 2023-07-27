package com.suviridedriver.di

import android.content.Context
import android.util.Log
import com.suviridedriver.api.APIServices
import com.suviridedriver.api.AuthInterceptor
import com.suviridedriver.utils.Constants.BASE_URL
import com.suviridedriver.utils.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.security.cert.CertPathValidatorException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit.Builder {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        interceptor: AuthInterceptor,
        @ApplicationContext context: Context
    ): OkHttpClient {

        return OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS) // connect timeout
            .writeTimeout(100, TimeUnit.SECONDS) // write timeout
            .readTimeout(100, TimeUnit.SECONDS) // read timeout
            // .addInterceptor(interceptor)
            .addInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    /*

                        var proceed: Response? = null
                        val original: Request = chain.request()
                        val requestBuilder: Request.Builder =
                            original.newBuilder().addHeader("Content-Type", "application/json")
                        val token = TokenManager.getToken(context)

                        if (token != null) {
                            requestBuilder.addHeader("Authorization", "Bearer $token")
                        }
                        try {
                            val request: Request = requestBuilder.build()
                            proceed = chain.proceed(request)
                        } catch (e: CertPathValidatorException) {
                            e.printStackTrace()
                        }
                        catch (e:Exception)
                        {
                            e.printStackTrace()

                        }
                         if (proceed!=null) {
                             return proceed!!
                        }
                        else
                         {
                             val requestBuilder: Request.Builder =
                                 original.newBuilder().addHeader("Content-Type", "application/json")
                             val token = TokenManager.getToken(context)

                             if (token != null) {
                                 requestBuilder.addHeader("Authorization", "Bearer $token")
                                 Log.d("token","Authorization Token - $token")
                             }
                             try {
                                 val request: Request = requestBuilder.build()
                                 proceed = chain.proceed(request)
                             } catch (e: CertPathValidatorException) {
                                 e.printStackTrace()
                             }
                             catch (e:Exception)
                             {
                                 e.printStackTrace()

                             }
                             return proceed!!
                         }*/

                    val token = TokenManager.getToken(context)
                    Log.i("token_a","Token - $token")
                    return chain.proceed(
                        chain.request().newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "Bearer $token").build()
                    )
                }
            })
            .build()
    }

    @Singleton
    @Provides
    fun providesAPI(
        retrofitBuilder: Retrofit.Builder,
        okHttpClient: OkHttpClient
    ): APIServices {
        return retrofitBuilder.client(okHttpClient).build().create(APIServices::class.java)
    }
}