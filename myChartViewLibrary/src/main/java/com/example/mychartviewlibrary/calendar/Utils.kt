package com.example.mychartviewlibrary.calendar

import android.content.Context
import com.example.mychartviewlibrary.calendar.data.DateItem
import java.util.*
import kotlin.math.roundToInt

object Utils {

    fun getDateFromCalToString(cal: Calendar):String {
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DATE)
        return "$year~$month~$day"
    }

    fun getDateFromYearMonthDay(dateItem: DateItem):String {
        return "${dateItem.year}~${dateItem.month}~${dateItem.date}"
    }

    fun getMyDateFromStringToDateItem(str:String): DateItem? {
        if (str.indexOf("~") != -1) {
            val dayOfMonth = str.substring(str.lastIndexOf("~")+1)
            val year = str.substring(0, str.indexOf("~"))
            val rest = str.substring(str.indexOf("~") + 1, str.length)
            val month = rest.substring(0, rest.indexOf("~"))
            return DateItem(year.toInt(), month.toInt(), dayOfMonth.toInt())
        }
        return null
    }

    fun convertDPtoPX(context: Context, dp:Int):Int {
        val density = context.resources.displayMetrics.density;
        return ((dp.toFloat()) * density).roundToInt();
    }

}