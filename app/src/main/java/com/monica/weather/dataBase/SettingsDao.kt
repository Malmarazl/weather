package com.monica.weather.dataBase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SettingsDao {
    @Query("SELECT * FROM DataSettings")
    fun getAll(): List<DataSettings>

    @Query("UPDATE DataSettings SET " +
            "sunny = :sunny, " +
            "cloudy = :cloudy, " +
            "snowy= :snowy, " +
            "rainy= :rainy, " +
            "tempMin= :tempMin, " +
            "tempMax = :tempMax ")
    fun updateTour(
        sunny: Boolean? = false,
        cloudy: Boolean? = false,
        snowy: Boolean? = false,
        rainy: Boolean? = false,
        tempMin: Int,
        tempMax: Int): Int

    @Insert
    fun insertAll(vararg settings: DataSettings)


    @Delete
    fun delete(settings: DataSettings)
}
