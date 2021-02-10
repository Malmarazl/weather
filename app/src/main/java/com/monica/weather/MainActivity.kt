package com.monica.weather

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.monica.weather.dataBase.DataSettings
import com.monica.weather.home.HomeFragment
import com.monica.weather.home.model.WeatherResponse
import com.monica.weather.settings.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var weatherResponse: WeatherResponse? = null
    var dataSettings:  List<DataSettings> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openFragment(HomeFragment())

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    openFragment(HomeFragment())
                }
                R.id.action_settings -> {
                    openFragment(SettingsFragment())
                }
            }
            false

        }
    }

    fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}