package com.example.weatherapp.utils

import java.math.RoundingMode

object HelperFunction {

    fun formattedDegree(temperature: Double?): CharSequence? {
        val temp = temperature as Double
        val temptoCelciuse = temp - 273.0
        val formatDegree = temptoCelciuse.toBigDecimal().setScale(2, RoundingMode.CEILING)
        return "$formatDegree °C"
    }
}