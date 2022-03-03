package com.example.mynotepad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mynotepad.activity.MainActivity
import com.example.personalcalendar.activity.PcalendarActivity
import com.example.sharecalendar.DataManager
import com.example.sharecalendar.activity.CalendarActivity




class AccessActivity : AppCompatActivity() {
    private lateinit var mPhoneNumber:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_access)
        DataManager.getNewNumberForHistory()
        DataManager.getAllHistoryData()
        DataManager.hcnt.observe(this, androidx.lifecycle.Observer {
            Log.i("kongyi3212", "hcnt is updated.")
            init()
//            MyNotification.doNotify(this)
        })

        DataManager.hList.observe(this, androidx.lifecycle.Observer {
            Log.i("kongyi1220TT", "hList is updated.")
//            init()
            var content = "none"
            if (it.get(it.lastIndex).arg2 == "access") {

                content = "번호 = ${it.get(it.lastIndex).arg1} 행위 = ${it.get(it.lastIndex).arg2}" +
                        " 대상 = ${it.get(it.lastIndex).arg3} 주체 = ${it.get(it.lastIndex).arg4}"

            } else if (it.get(it.lastIndex).arg2 == "pcal-schedule-new") {
                content = "번호 = ${it.get(it.lastIndex).arg1} 행위 = ${it.get(it.lastIndex).arg2}" +
                        " arg3 = ${it.get(it.lastIndex).arg3} arg4 = ${it.get(it.lastIndex).arg4}" +
                        " arg5 = ${it.get(it.lastIndex).arg5}"
            }

            MyNotification.doNotify(this, content)
        })

        mPhoneNumber = DataManager.getLineNumber(this, this) // context 정보가 null이 아니려면 onCreate 에서 this를 넣어줘야.
        // onCreate 이전에는 null이다.
    }



    private fun init() {
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
            DataManager.putSingleHistory(this, "access", "shareCalendar", mPhoneNumber)
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