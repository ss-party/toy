package com.example.sharecalendar.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.example.model.DataManager
import com.example.sharecalendar.R
import com.example.model.Utils
import com.example.model.data.Schedule

class DayActivity : AppCompatActivity() {
    private lateinit var inputButtonView: Button
    private lateinit var closeButtonView: Button
    private lateinit var contentView: EditText
    private lateinit var titleView: EditText
    private lateinit var dateView: DatePicker
    private var mSelectedColor:String = ""

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
            val day = Utils.getDateFromStringToCal(schedule.date)
            dateView.init(day!!.year, day.month, day.day, null)
        }

        inputButtonView.setOnClickListener {
            date = "${ dateView.year }~${ dateView.month }~${ dateView.dayOfMonth }"
            Log.i("kongyi1220A", "before id = " + schedule!!.id)
            DataManager.putSingleSchedule("id_list", date, titleView.text.toString(), contentView.text.toString(), mSelectedColor, schedule.id)
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

        when (schedule?.color) {
            "red" -> {
                findViewById<RadioButton>(R.id.radio_button_red).isChecked = true
                mSelectedColor = "red"
            }
            "orange" -> {
                findViewById<RadioButton>(R.id.radio_button_orange).isChecked = true
                mSelectedColor = "orange"
            }
            "yellow" -> {
                findViewById<RadioButton>(R.id.radio_button_yellow).isChecked = true
                mSelectedColor = "yellow"
            }
            "green" -> {
                findViewById<RadioButton>(R.id.radio_button_green).isChecked = true
                mSelectedColor = "green"
            }
            "blue" -> {
                findViewById<RadioButton>(R.id.radio_button_blue).isChecked = true
                mSelectedColor = "blue"
            }
            "purple" -> {
                findViewById<RadioButton>(R.id.radio_button_purple).isChecked = true
                mSelectedColor = "purple"
            }
        }

        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_button_red -> mSelectedColor = "red"
                R.id.radio_button_orange -> mSelectedColor = "orange"
                R.id.radio_button_yellow -> mSelectedColor = "yellow"
                R.id.radio_button_green -> mSelectedColor = "green"
                R.id.radio_button_blue -> mSelectedColor = "blue"
                R.id.radio_button_purple -> mSelectedColor = "purple"
            }
        }
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    fun onClickClose() {
        finish()
    }
}