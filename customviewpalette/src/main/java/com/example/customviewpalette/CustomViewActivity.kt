package com.example.customviewpalette

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mychartviewlibrary.calendar.MyCalendarView

class CustomViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_view)
        val calendarView = findViewById<MyCalendarView>(R.id.myCalendarView)
//        initializeCalendar(calendarView)
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
