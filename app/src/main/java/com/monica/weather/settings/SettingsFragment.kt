package com.monica.weather.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.monica.weather.CallbackSwitch
import com.monica.weather.R
import com.monica.weather.dataBase.DataSettings
import com.monica.weather.dataBase.WeatherDataBase
import kotlinx.android.synthetic.main.screen_settings.*
import kotlinx.coroutines.*
import java.lang.Exception

class SettingsFragment: Fragment() {
    private val listWeatherSettings: List<WeatherSwitch> = listOf(
        WeatherSwitch(SUNNY, false),
        WeatherSwitch(CLOUDY, false),
        WeatherSwitch(SNOWY,false),
        WeatherSwitch(RAINY,false)
    )
    private val adapter = ItemSwitchAdapter(listWeatherSettings)
    private val recyclerView by lazy { recycler }

    private var db: WeatherDataBase? = null
    private var dataDao: List<DataSettings>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.screen_settings, container, false)
    }

    private var switchCallback = object: CallbackSwitch {
        override fun sendSwitchStatus(title: String, isChecked: Boolean) {
            listWeatherSettings.forEach {
                if(it.title == title) it.value = isChecked
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter.setCallback(switchCallback)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        super.onViewCreated(view, savedInstanceState)
        db = WeatherDataBase.getInstance(requireContext())

        db?.let {
            GlobalScope.launch(Dispatchers.Main) {
                val dao = async(Dispatchers.IO) { it.settingsDao().getAll() }
                dataDao = dao.await()
                loadDataChanged(dao.await())
            }
        }


        seekMin.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    tempMinValue.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekMax.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tempMaxValue.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        buttonSave.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                if(dataDao?.isNotEmpty() == true) {
                    async(Dispatchers.IO) {
                        try {
                            db?.settingsDao()?.updateTour(
                                listWeatherSettings[0].value,
                                listWeatherSettings[1].value,
                                listWeatherSettings[2].value,
                                listWeatherSettings[3].value,
                                tempMinValue.text.toString().toInt(),
                                tempMaxValue.text.toString().toInt()
                            )
                        } catch (e: Exception) {
                            Toast.makeText(context, "Something went wrong saving your settings", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val dataSettings = DataSettings(0,
                        sunny = listWeatherSettings[0].value,
                        cloudy = listWeatherSettings[1].value,
                        snowy = listWeatherSettings[2].value,
                        rainy = listWeatherSettings[3].value,
                        tempMin =  tempMinValue.text.toString().toInt(),
                        tempMax = tempMaxValue.text.toString().toInt()
                    )

                    async(Dispatchers.IO) {
                        try {
                            db?.settingsDao()?.insertAll(dataSettings)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Something went wrong saving your settings", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                Toast.makeText(context, "Your options have been save", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun loadDataChanged(dataDao: List<DataSettings>) {
        if (dataDao.isNotEmpty()) {
            val data = dataDao[0]

            seekMin.progress = data.tempMin
            tempMinValue.text = data.tempMin.toString()

            seekMax.progress = data.tempMax
            tempMaxValue.text = data.tempMax.toString()

            for(item in adapter.getItems()){
                when (item.title) {
                    SUNNY -> item.value = data.sunny
                    CLOUDY -> item.value = data.cloudy
                    SNOWY -> item.value = data.snowy
                    RAINY -> item.value = data.rainy
                }
            }
            adapter.notifyDataSetChanged()
        }
    }

    companion object {
        private const val SUNNY = "sunny"
        private const val RAINY = "rainy"
        private const val CLOUDY = "cloudy"
        private const val SNOWY = "snowy"
    }
}