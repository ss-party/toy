package com.example.mychartviewlibrary.calendar.list

import com.example.model.data.Schedule

interface OnScheduleItemClickListener {
    fun onItemClick(schedule: Schedule)
}