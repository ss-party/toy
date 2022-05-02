package com.example.mychartviewlibrary.calendar.data

import android.view.View
import android.widget.TextView
import com.example.common.ContextHolder

// ContextHolder?
data class DateItem(
    val year: Int = 0,
    val month: Int = 0,
    val date: Int = 0,
    val role: Int = 0, // 0이면 문자역할만, 1이면 날짜 역할
    val text: String,
    val view: View = TextView(ContextHolder.getContext())
) {
    fun getKey():Int {
        return year*10000 + month*100 + date
    }
}