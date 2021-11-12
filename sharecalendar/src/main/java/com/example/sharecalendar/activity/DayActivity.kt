package com.example.sharecalendar.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import com.example.sharecalendar.DataManager
import com.example.sharecalendar.R
import com.example.sharecalendar.Utils
import com.example.sharecalendar.data.Schedule

class DayActivity : AppCompatActivity() {
    private lateinit var inputButtonView: Button
    private lateinit var closeButtonView: Button
    private lateinit var contentView: EditText
    private lateinit var titleView: EditText
    private lateinit var dateView: DatePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day)
        inputButtonView = findViewById(R.id.inputBtn)
        closeButtonView = findViewById(R.id.closeBtn)
        contentView = findViewById(R.id.content)
        titleView = findViewById(R.id.title)
        dateView = findViewById(R.id.datePicker)

        var date = ""

        val schedule = intent.getSerializableExtra("info") as? Schedule
        Log.i("kongyi1220", "hey!!! title = " + schedule?.title)
        titleView.setText(schedule?.title)
        contentView.setText(schedule?.content)
        if (schedule != null) {
            val day = Utils.getDateFromStringToCal(schedule?.date!!)
            dateView.init(day!!.year, day.month, day.day, null)
        }

        inputButtonView.setOnClickListener {
            date = "${ dateView.year }~${ dateView.month }~${ dateView.dayOfMonth }"
            Log.i("kongyi1220", "date = " + date)
            DataManager.putSingleSchedule(date, titleView.text.toString(), contentView.text.toString(), "", schedule!!.id)
            //conditionRef.setValue(editText.text.toString())
            onClickClose()
        }
        closeButtonView.setOnClickListener {
            onClickClose()
        }

//        deleteButtonView.setOnClickListener {
//            date = "${ dateView.year }~${ dateView.month }~${ dateView.dayOfMonth }"
//            Log.i("kongyi1220", "removed")
//            DataManager.removeSingleSchedule(date)
//            onClickClose()
//        }
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    fun onClickClose() {
        finish()
    }
}