package com.example.customviewpalette

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.model.DataManager
import com.example.mychartviewlibrary.calendar.MyCalendarView



class CustomViewActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_view)
        val calendarView = findViewById<MyCalendarView>(R.id.myCalendarView)
//        initializeCalendar(calendarView)
        Log.i("kongyi0503", "onCreate()")
        DataManager.getAllScheduleData("id_list")
        Log.i("kongyi0503", "calendarView = ${calendarView}")
        DataManager.dataList.observe(this, androidx.lifecycle.Observer {
//            mScheduleList = it
//            putDataOnCalendar()
            Log.i("kongyi0504", "observe dataList it = ${it}")
            // 클릭 리스너를 함께 등록해줘야 할 듯
            calendarView.setOnItemClickListener(it)
            calendarView.setSchedules(it)

//            calView.addDecorators(
//                SundayDecorator(),
//                SaturdayDecorator(),
//                TodayDecorator()
//            )
        })
    }

//    private fun initializeCalendar(calView: MyCalendarView) {
//        calView.setDateTextAppearance(1); // 날짜 선택했을 때, 날짜 text가 색깔이 검은 색이 되도록 함. 이거 안쓰면 흰색됨.

//        calView.state().edit()
//            .setFirstDayOfWeek(Calendar.SUNDAY)
//            .setMinimumDate(CalendarDay.from(2017, 0, 1))
//            .setMaximumDate(CalendarDay.from(2030, 11, 31))
//            .setCalendarDisplayMode(CalendarMode.MONTHS)
//            .commit()

//        calView.addDecorators(
//            SundayDecorator(),
//            SaturdayDecorator(),
//            TodayDecorator()
//        )
//    }
}
