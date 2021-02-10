package com.monica.weather.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DataSettings::class], version = 1)
abstract class WeatherDataBase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao

    companion object {

        private var INSTANCE: WeatherDataBase? = null

        fun getInstance(context: Context): WeatherDataBase {
            return INSTANCE?.run {
                this
            } ?: run {
                 Room.databaseBuilder(
                    context,
                    WeatherDataBase::class.java, "data-Settings"
                ).build()
            }
        }
    }
}
