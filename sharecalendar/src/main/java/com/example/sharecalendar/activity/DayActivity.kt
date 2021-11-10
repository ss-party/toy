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
    private lateinit var deleteButtonView: Button
    private lateinit var contentView: EditText
    private lateinit var titleView: EditText
    private lateinit var dateView: DatePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day)
        inputButtonView = findViewById(R.id.inputBtn)
        closeButtonView = findViewById(R.id.closeBtn)
        deleteButtonView = findViewById(R.id.deleteBtn)
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

            DataManager.putSingleSchedule(date, titleView.text.toString(), contentView.text.toString())
            //conditionRef.setValue(editText.text.toString())
            onClickClose()
        }
        closeButtonView.setOnClickListener {
            onClickClose()
        }
        if (schedule != null) {
            deleteButtonView.visibility = View.VISIBLE
        } else {
            deleteButtonView.visibility = View.GONE
        }

        deleteButtonView.setOnClickListener {
            date = "${ dateView.year }~${ dateView.month }~${ dateView.dayOfMonth }"
            Log.i("kongyi1220", "removed")
            DataManager.removeSingleSchedule(date)
            onClickClose()
        }
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    fun onClickClose() {
        /*
        데이터 삭제
데이터를 삭제하는 가장 간단한 방법은 해당 데이터 위치의 참조에 removeValue()를 호출하는 것입니다.

setValue() 또는 updateChildren() 등의 다른 쓰기 작업 값으로 null을 지정하여 삭제할 수도 있습니다. updateChildren()에 이 방법을 사용하면 API 호출 한 번으로 여러 하위 항목을 삭제할 수 있습니다.
         */
        finish()
    }
}