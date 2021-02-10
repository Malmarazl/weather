package com.monica.weather.home.services

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class OpenWeatherServiceAdapter {
    private var API_SERVICE: OpenWeatherService? = null

    fun getApiService(): OpenWeatherService? {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        val baseUrl = "http://api.openweathermap.org/"
        if (API_SERVICE == null) {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
            API_SERVICE = retrofit.create(OpenWeatherService::class.java)
        }
        return API_SERVICE
    }
}