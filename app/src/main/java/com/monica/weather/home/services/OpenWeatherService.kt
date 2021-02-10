package com.monica.weather.home.services

import com.monica.weather.home.model.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

val OPEN_WEATHER_API_KEY = "f678fd36293748a065b33b36cc8a817a"

interface OpenWeatherService {
    @GET("data/2.5/weather?")
    open fun getWeather(
        @Query("q") cityName: String,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = OPEN_WEATHER_API_KEY
    ) : Call<WeatherResponse>
}