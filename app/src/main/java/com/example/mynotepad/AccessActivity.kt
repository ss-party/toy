package com.example.mynotepad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.mynotepad.activity.MainActivity
import com.example.personalcalendar.activity.PcalendarActivity
import com.example.sharecalendar.activity.CalendarActivity

class AccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_access)
        findViewById<Button>(R.id.alarmNotiBtn).setOnClickListener {
            startActivity(Intent(this, AlarmMainActivity::class.java))
        }
        findViewById<Button>(R.id.myMemoBtn).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        findViewById<Button>(R.id.shareCalendarBtn).setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }
        findViewById<Button>(R.id.personalCalendarBtn).setOnClickListener {
            startActivity(Intent(this, PcalendarActivity::class.java))
        }

    }
}