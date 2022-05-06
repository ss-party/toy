package com.example.customviewpalette

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.common.ContextHolder
import com.example.model.DataManager
import com.example.model.data.Schedule
import com.example.mychartviewlibrary.calendar.MyCalendarView
import com.example.mychartviewlibrary.calendar.list.OnScheduleItemClickListener
import com.example.sharedcalendar.activity.DayActivity


class CustomViewActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_view)
        val calendarView = findViewById<MyCalendarView>(R.id.myCalendarView)
        DataManager.getAllScheduleData("id_list")
        DataManager.dataList.observe(this, androidx.lifecycle.Observer {
            val listener = object : OnScheduleItemClickListener {
                override fun onItemClick(schedule: Schedule) {
                    val intent = Intent(this@CustomViewActivity, DayActivity::class.java)
                    Log.i("kongyi0505", "schedule = $schedule");
                    intent.putExtra("info", schedule);
                    startActivity(intent);
                }
            }
            calendarView.initializeCalendar()
            calendarView.setOnItemClickListener(it, listener)
            calendarView.setSchedules(it)
            if (calendarView.mCurrentDate != null) {
                calendarView.loadDataAtList(it, calendarView.mCurrentDate!!, listener)
            }
        })
    }
}
