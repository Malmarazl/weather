package com.monica.weather.dataBase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DataSettings(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "sunny") var sunny: Boolean,
    @ColumnInfo(name = "cloudy") var cloudy: Boolean,
    @ColumnInfo(name = "snowy") var snowy: Boolean,
    @ColumnInfo(name = "rainy") var rainy: Boolean,
    @ColumnInfo(name = "tempMin") var tempMin: Int,
    @ColumnInfo(name = "tempMax") var tempMax: Int
)
