package com.monica.weather

interface CallbackSwitch {
    fun sendSwitchStatus(title: String, isChecked: Boolean)
}