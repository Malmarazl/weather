package com.monica.weather.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.monica.weather.CallbackSwitch
import com.monica.weather.R

class ItemSwitchAdapter (private val itemList: List<WeatherSwitch>) : RecyclerView.Adapter<ItemSwitchAdapter.ViewHolder>() {

    private var callback : CallbackSwitch? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_switch, parent, false)

        return ViewHolder(view)
    }

    fun setCallback(callbackSwitch: CallbackSwitch){
        this.callback = callbackSwitch
    }

    fun getItems() : List<WeatherSwitch> {
        return itemList
    }

    override fun getItemCount(): Int = itemList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById<TextView>(R.id.title_switch_weather)
        val switch: SwitchCompat = view.findViewById<SwitchCompat>(R.id.switch_weather)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.title.text = item.title
        holder.switch.isChecked = item.value

        holder.switch.setOnClickListener{
            callback?.sendSwitchStatus(holder.title.text.toString(), holder.switch.isChecked)
        }
    }
}