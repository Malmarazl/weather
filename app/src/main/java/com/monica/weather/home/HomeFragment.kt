package com.monica.weather.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.monica.weather.MainActivity
import com.monica.weather.R
import com.monica.weather.dataBase.DataSettings
import com.monica.weather.dataBase.WeatherDataBase
import com.monica.weather.home.model.WeatherResponse
import com.monica.weather.home.services.OpenWeatherServiceAdapter
import com.monica.weather.settings.SettingsFragment
import kotlinx.android.synthetic.main.screen_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment: Fragment() {

    private var db: WeatherDataBase? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.screen_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.cities_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            list_cities.adapter = adapter
        }

        list_cities.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val city = parent?.getItemAtPosition(position).toString()
                connectionToDataBase(city)
                Toast.makeText(context, "Weather in  $city", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

    }

    private fun checkDataBase() {
        val isMatch: Boolean
        if ((activity as MainActivity).dataSettings.isNotEmpty()) {

            if((activity as MainActivity).weatherResponse != null) {
                isMatch = matchData(
                    (activity as MainActivity).weatherResponse!!,
                    (activity as MainActivity).dataSettings
                )

                if(isMatch) {
                    result_text.text = "Today is a good day for you :)"
                    lottie.setAnimation(R.raw.clear_day)
                    lottie.playAnimation()

                } else {
                    result_text.text = "Today is a bad day for you :("
                    lottie.setAnimation(R.raw.thunderstorm_day)
                    lottie.playAnimation()
                }

            } else  Toast.makeText(context, "No weather response", Toast.LENGTH_SHORT).show()

        } else {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Attention")
            builder.setMessage("Set your preferences")
            builder.setNeutralButton("Go to settings") { dialog, which ->
                (activity as MainActivity).openFragment(SettingsFragment())
            }

            builder.show()
        }
    }

    private fun connectionToDataBase(city: String) {
        db = WeatherDataBase.getInstance(requireContext())

        db?.let {
            GlobalScope.launch(Dispatchers.Main) {
                val dao = async(Dispatchers.IO) { it.settingsDao().getAll() }
                (activity as MainActivity).dataSettings = dao.await()
                connectionToWeatherApi(city)
            }
        }
    }

    private fun connectionToWeatherApi(city: String) {
        if(((activity as MainActivity).weatherResponse == null || (activity as MainActivity).weatherResponse?.name != city)
            && (activity as MainActivity).dataSettings.isNotEmpty()) {
            val call = OpenWeatherServiceAdapter().getApiService()?.getWeather(city)

            call?.enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.code() == 200) {
                        (activity as MainActivity).weatherResponse = response.body()

                        checkDataBase()
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Toast.makeText(context, "Connection fail ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            checkDataBase()
        }
    }


    private fun matchData(response: WeatherResponse, dataDao: List<DataSettings>): Boolean {
        val max = dataDao[0].tempMax.toFloat()
        val min = dataDao[0].tempMin.toFloat()

        val weather: Boolean =
            when(response.weather[0].main) {
                "Clear" -> dataDao[0].sunny
                "Clouds" -> dataDao[0].cloudy
                "Snow" -> dataDao[0].snowy
                "Rain" -> dataDao[0].rainy
                else -> false
            }

        val temperature: Boolean = response.main.temp < max && response.main.temp > min

        return weather && temperature
    }

}
