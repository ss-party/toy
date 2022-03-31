package com.example.sstoy

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.model.DataManager
import com.example.sstoy.activity.MainActivity
import com.example.paperweight.PaperWeightActivity
import com.example.sharedcalendar.activity.CalendarActivity

class AccessActivity : AppCompatActivity() {
    private lateinit var mPhoneNumber:String
    var isAdmin:Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_access)
        DataManager.getNewNumberForHistory()
        DataManager.getNotice()

//        DataManager.getAllHistoryData()
        DataManager.hcnt.observe(this, androidx.lifecycle.Observer {
            Log.i("kongyi3212", "hcnt is updated.")
            init()
        })

        DataManager.notice.observe(this, androidx.lifecycle.Observer {
            showNoticeDialog(DataManager.notice.value.toString())
        })

        /*  From Google's docs on Android 8.0 behavior changes:

            The system allows apps to call Context.startForegroundService()
            even while the app is in the background.
            However, the app must call that service's startForeground()
            method within five seconds after the service is created.` */

        if (DataManager.getNotificationState(this)) {
            val intent = Intent(applicationContext, MyService::class.java)
            intent.putExtra("command", "show")
            startForegroundService(intent) // foreground service 실행을 위해 이것만 있으면 됨. 윗줄의 startService(intent)는 필요 없음.
        }

        mPhoneNumber = DataManager.getLineNumber(this, this) // context 정보가 null이 아니려면 onCreate 에서 this를 넣어줘야.
        // onCreate 이전에는 null이다.
        if (mPhoneNumber == "+821027740931"
            || mPhoneNumber == "+821040052032") {
            isAdmin = true
        }
    }

    private fun showNoticeDialog(content:String) {
        Log.i("kongyi1220", "showNoticeDialog()")
         AlertDialog.Builder(this)
            .setMessage(content)     // 제목 부분 (직접 작성)
            .setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, i ->
                Toast.makeText(applicationContext, "확인 누름", Toast.LENGTH_SHORT).show()
            }).show()
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
        if (isAdmin) {
            findViewById<Button>(R.id.alarmNotiBtn).visibility = View.VISIBLE
            findViewById<Button>(R.id.myMemoBtn).visibility = View.VISIBLE
            findViewById<Button>(R.id.shareCalendarBtn).visibility = View.VISIBLE
            findViewById<Button>(R.id.historyManagerBtn).visibility = View.VISIBLE
            findViewById<Button>(R.id.paperWeightBtn).visibility = View.VISIBLE
        }

        findViewById<Button>(R.id.alarmNotiBtn).setOnClickListener {
            Log.i("kongyi1220", "alarmNotiBtn clicked")
            startActivity(Intent(this, AlarmMainActivity::class.java))
            DataManager.putSingleHistory(this,"access", "alarmNoti", mPhoneNumber)
        }
        findViewById<Button>(R.id.myMemoBtn).setOnClickListener {
            Log.i("kongyi1220", "myMemoBtn clicked")

            startActivity(Intent(this, MainActivity::class.java))
            DataManager.putSingleHistory(this, "access", "myMemo", mPhoneNumber)
        }
        findViewById<Button>(R.id.shareCalendarBtn).setOnClickListener {
            Log.i("kongyi1220", "shareCalendarBtn clicked")
            startActivity(Intent(this, CalendarActivity::class.java))
            DataManager.putSingleHistory(this, "access", "sharedCalendar", mPhoneNumber)
        }
        findViewById<Button>(R.id.historyManagerBtn).setOnClickListener {
            Log.i("kongyi1220", "historyManagerBtn clicked")
            startActivity(Intent(this, HistoryActivity::class.java))
            DataManager.putSingleHistory(this, "access", "historyManager", mPhoneNumber)
        }
        findViewById<Button>(R.id.paperWeightBtn).setOnClickListener {
            Log.i("kongyi1220", "paperWeightBtn clicked")
            startActivity(Intent(this, PaperWeightActivity::class.java))
            DataManager.putSingleHistory(this, "access", "historyManager", mPhoneNumber)
        }
    }
}