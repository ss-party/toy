package com.example.customviewpalette

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.model.DataManager
import com.example.model.data.Schedule
import com.example.mychartviewlibrary.calendar.MyCalendarView
import com.example.mychartviewlibrary.calendar.OnAddBtnClickListener
import com.example.mychartviewlibrary.calendar.list.OnScheduleItemClickListener
import com.example.sharedcalendar.activity.DayActivity


class CustomViewActivity : AppCompatActivity() {
    val TAG = "CustomViewActivity"
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_view)
        val calendarView = findViewById<MyCalendarView>(R.id.myCalendarView)
        DataManager.dataList.observe(this, androidx.lifecycle.Observer { scheduleList ->
            Log.i(TAG, "dataList observe")
            val scheduleItemClickListener = object : OnScheduleItemClickListener {
                override fun onItemClick(schedule: Schedule) {
                    val intent = Intent(this@CustomViewActivity, DayActivity::class.java)
                    Log.i("kongyi0505", "schedule = $schedule");
                    intent.putExtra("info", schedule);
                    startActivity(intent);
                }
            }
            // todo : it should be optimized in sometime
            calendarView.setOnItemClickListener(scheduleList, scheduleItemClickListener)
            calendarView.setSchedules(scheduleList)
            calendarView.refresh() // it is needed
            calendarView.mCurrentDate?.let {
                calendarView.loadDataAtList(scheduleList, calendarView.mCurrentDate!!, scheduleItemClickListener)
            }
            val addBtnListener = object : OnAddBtnClickListener {
                override fun onItemClick(date: String) {
                    Log.i("kongyi0506", "date = $date")
                    val intent = Intent(this@CustomViewActivity, DayActivity::class.java)
                    val schedule = Schedule("no_id", date, "", "", "")
                    intent.putExtra("info", schedule)
                    startActivity(intent);
                }
            }
            calendarView.setAddScheduleBtn(addBtnListener)
            Log.i("kongyi0507", "observe complete time = ${System.currentTimeMillis()}")
        })
    }
}
