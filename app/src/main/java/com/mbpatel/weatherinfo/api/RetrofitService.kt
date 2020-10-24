package com.mbpatel.weatherinfo.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Weather API communication setup via Retrofit.
 */
class RetrofitService {
    companion object {
        private const val BASE_URL = "http://api.openweathermap.org/"
        private val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()

        /**
         * Use for service generator create
         */
        fun create(): ApiGenerator   {
            httpClient.connectTimeout(60, TimeUnit.SECONDS)
            httpClient.writeTimeout(60, TimeUnit.SECONDS)
            httpClient.readTimeout(90, TimeUnit.SECONDS)


            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(logger)

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiGenerator::class.java)
        }

    }
}
