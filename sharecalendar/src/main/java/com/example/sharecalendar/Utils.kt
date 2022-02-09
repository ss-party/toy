package com.example.sharecalendar

import android.util.Log
import android.widget.TextView
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    fun getDateFromStringToCal(str:String): CalendarDay? {
        if (str.indexOf("~") != -1) {
            val dayOfMonth = str.substring(str.lastIndexOf("~")+1)
            val year = str.substring(0, str.indexOf("~"))
            val rest = str.substring(str.indexOf("~") + 1, str.length)
            val month = rest.substring(0, rest.indexOf("~"))
            val day: CalendarDay = CalendarDay.from(year.toInt(), month.toInt(), dayOfMonth.toInt())
            return day
        }
        return null
    }

    fun getDateFromCalToString(cal:Calendar):String {
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DATE)
        return "$year~$month~$day"
    }

    /**
     * SHA-256으로 해싱하는 메소드
     *
     * @param bytes
     * @return
     * @throws NoSuchAlgorithmException
     */
    fun sha256(msg:String) : ByteArray {
        val md = MessageDigest.getInstance("SHA-256")

        md.update(msg.toByteArray());

        return md.digest();
    }

    /**
     * 바이트를 헥사값으로 변환한다, type 1
     *
     * @param bytes
     * @return
     */
    fun bytesToHex1(bytes:ByteArray):String {
        val builder = StringBuilder();
        for (b in bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

}