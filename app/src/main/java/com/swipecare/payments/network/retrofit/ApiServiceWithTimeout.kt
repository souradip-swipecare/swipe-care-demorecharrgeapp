package com.swipecare.payments.network.retrofit

import com.swipecare.payments.model.AEPSMiniStatementResponse
import com.swipecare.payments.model.bodypara.MinistBody
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

var okHttpClient = OkHttpClient().newBuilder()
    .connectTimeout(90, TimeUnit.SECONDS)
    .readTimeout(90, TimeUnit.SECONDS)
    .writeTimeout(90, TimeUnit.SECONDS)
    .build()

val retrofitForSwipeApiWithTimeout = Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(okHttpClient)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

interface SwipeApiWithTimeOut {
    @POST("api/android/aeps/MS")
    suspend fun miniStatement(
        @Body
        AinistBody: MinistBody
    ): AEPSMiniStatementResponse
}

object SwipeApiWithTimeoutObj {
    val retrofitService by lazy {
        retrofitForSwipeApiWithTimeout.create(SwipeApiWithTimeOut::class.java)
    }
}