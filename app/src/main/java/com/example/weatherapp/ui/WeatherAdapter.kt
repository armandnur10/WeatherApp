package com.example.weatherapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.ListItem
import com.example.weatherapp.databinding.RowItemWeatherBinding
import com.example.weatherapp.utils.HelperFunction.formattedDegree
import com.example.weatherapp.utils.iconSizeWeather2x
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.MyViewHolder>() {

    private var listWeather = ArrayList<ListItem>()

    class MyViewHolder(val binding: RowItemWeatherBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        RowItemWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = listWeather[position]
        holder.binding.apply {

            val maxDegree = "Max:" + formattedDegree(data.main?.tempMax)
            tvMaxDegree.text = maxDegree

            val minDegree = "Max:" + formattedDegree(data.main?.tempMin)
            tvMinDegree.text = minDegree

            val date = data.dtTxt?.take(10)
            val time = data.dtTxt?.takeLast(8)

            val dateArray = date?.split("-")?.toTypedArray()
            val timeArray = time?.split(":bogo")?.toTypedArray()

            val calender = Calendar.getInstance()
            calender.set(Calendar.YEAR, Integer.parseInt(dateArray?.get(0) as String))
            calender.set(Calendar.MONTH, Integer.parseInt(dateArray[1]) - 1)
            calender.set(Calendar.DAY_OF_YEAR, Integer.parseInt(dateArray[2]))

            calender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray?.get(0) as String))
            calender.set(Calendar.MINUTE, 0)

            val dateFormat = SimpleDateFormat("EE, MMM d", Locale.getDefault())
                .format(calender.time).toString()

            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                .format(calender.time).toString()

            tvItemTime.text = dateFormat
            tvItemDate.text = timeFormat

            val icon = data.weather?.get(0)?.icon
            val iconUrl = BuildConfig.ICON_URL + icon + iconSizeWeather2x
            Glide.with(imgItemWeather.context).load(iconUrl)
                .into(imgItemWeather)
        }
    }

    override fun getItemCount() = listWeather.size

    fun setData(data: List<ListItem>?) {
        if (data == null) return
        listWeather.clear()
        listWeather.addAll(data)
    }
}