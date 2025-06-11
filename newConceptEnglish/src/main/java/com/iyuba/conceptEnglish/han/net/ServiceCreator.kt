package com.iyuba.conceptEnglish.han.net

import com.iyuba.configation.Constant
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceCreator {
    private val BASE_URL = "http://apps.${Constant.IYUBA_CN_IN}/"
    private const val timeOut=10L
    private val client = OkHttpClient.Builder()
        .callTimeout(timeOut, TimeUnit.SECONDS)
        .readTimeout(timeOut, TimeUnit.SECONDS)
        .writeTimeout(timeOut, TimeUnit.SECONDS)
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified T> create(): T = create(T::class.java)
}