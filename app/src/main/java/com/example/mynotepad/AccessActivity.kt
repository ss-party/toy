package com.example.mynotepad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import com.example.model.DataManager
import com.example.mynotepad.activity.MainActivity
import com.example.paperweight.PaperWeightActivity
import com.example.personalcalendar.activity.PcalendarActivity
import com.example.sharedcalendar.activity.CalendarActivity
import kotlin.properties.Delegates

class AccessActivity : AppCompatActivity() {
    private lateinit var mPhoneNumber:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_access)
        DataManager.getNewNumberForHistory()
//        DataManager.getAllHistoryData()
        DataManager.hcnt.observe(this, androidx.lifecycle.Observer {
            Log.i("kongyi3212", "hcnt is updated.")
            init()
        })
        val intent = Intent(applicationContext, MyService::class.java)
        intent.putExtra("command", "show")
        startForegroundService(intent) // foreground service 실행을 위해 이것만 있으면 됨. 윗줄의 startService(intent)는 필요 없음.
        mPhoneNumber = DataManager.getLineNumber(this, this) // context 정보가 null이 아니려면 onCreate 에서 this를 넣어줘야.
        // onCreate 이전에는 null이다.
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menuSettingBtn-> startActivity(Intent(this, SettingActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun init() {
//        findViewById<Button>(R.id.alarmNotiBtn).visibility = View.GONE
//        findViewById<Button>(R.id.myMemoBtn).visibility = View.GONE
//        findViewById<Button>(R.id.shareCalendarBtn).visibility = View.GONE
//        findViewById<Button>(R.id.personalCalendarBtn).visibility = View.GONE
//        findViewById<Button>(R.id.historyManagerBtn).visibility = View.GONE
//        findViewById<Button>(R.id.paperWeightBtn).visibility = View.GONE
//
//        if (notificationEnable) {
//            findViewById<Button>(R.id.alarmNotiBtn).visibility = View.VISIBLE
//        }

        findViewById<Button>(R.id.alarmNotiBtn).setOnClickListener {
            startActivity(Intent(this, AlarmMainActivity::class.java))
            DataManager.putSingleHistory(this,"access", "alarmNoti", mPhoneNumber)
        }
        findViewById<Button>(R.id.myMemoBtn).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            DataManager.putSingleHistory(this, "access", "myMemo", mPhoneNumber)
        }
        findViewById<Button>(R.id.shareCalendarBtn).setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
            DataManager.putSingleHistory(this, "access", "sharedCalendar", mPhoneNumber)
        }
        findViewById<Button>(R.id.personalCalendarBtn).setOnClickListener {
            startActivity(Intent(this, PcalendarActivity::class.java))
            DataManager.putSingleHistory(this, "access", "personalCalendar", mPhoneNumber)
        }
        findViewById<Button>(R.id.historyManagerBtn).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
            DataManager.putSingleHistory(this, "access", "historyManager", mPhoneNumber)
        }
        findViewById<Button>(R.id.paperWeightBtn).setOnClickListener {
            startActivity(Intent(this, PaperWeightActivity::class.java))
            DataManager.putSingleHistory(this, "access", "historyManager", mPhoneNumber)
        }
    }
}