package com.example.mynotepad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.mynotepad.activity.MainActivity
import com.example.personalcalendar.activity.PcalendarActivity
import com.example.sharecalendar.DataManager
import com.example.sharecalendar.DataManager.putSingleHistory
import com.example.sharecalendar.activity.CalendarActivity
import com.example.sharecalendar.activity.SaturdayDecorator
import com.example.sharecalendar.activity.SundayDecorator
import com.example.sharecalendar.activity.TodayDecorator

class AccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_access)
        DataManager.getNewNumberForHistory()
        DataManager.hcnt.observe(this, androidx.lifecycle.Observer {
            Log.i("kongyi3212", "hcnt is updated.")
            init()
        })
    }

    private fun init() {
        findViewById<Button>(R.id.alarmNotiBtn).setOnClickListener {
            startActivity(Intent(this, AlarmMainActivity::class.java))
//            putSingleHistory("access", "alarmNoti")
        }
        findViewById<Button>(R.id.myMemoBtn).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
//            putSingleHistory("access", "myMemo")
        }
        findViewById<Button>(R.id.shareCalendarBtn).setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
//            putSingleHistory("access", "shareCalendar")
        }
        findViewById<Button>(R.id.personalCalendarBtn).setOnClickListener {
            startActivity(Intent(this, PcalendarActivity::class.java))
//            putSingleHistory("access", "personalCalendar")
        }
        findViewById<Button>(R.id.historyManagerBtn).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
//            putSingleHistory("access", "historyManager")
        }
    }
}