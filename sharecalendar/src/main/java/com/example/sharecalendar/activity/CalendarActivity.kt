package com.example.sharecalendar

import android.graphics.Color
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.*
import android.text.style.ForegroundColorSpan

import com.prolificinteractive.materialcalendarview.DayViewFacade

import com.prolificinteractive.materialcalendarview.DayViewDecorator
import android.text.style.RelativeSizeSpan

import android.graphics.Typeface

import android.text.style.StyleSpan

import android.app.Activity
import android.content.Intent

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sharecalendar.activity.DayActivity
import com.example.sharecalendar.data.Schedule
import com.example.sharecalendar.list.DayListAdapter
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import kotlin.collections.HashMap


class CalendarActivity : AppCompatActivity() {

    private var mScheduleList: ArrayList<Schedule>? = null
    private val map = HashMap<CalendarDay, Schedule>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        val calView = findViewById<MaterialCalendarView>(R.id.calendarView)
        calView.state().edit()
            .setFirstDayOfWeek(Calendar.SUNDAY)
            .setMinimumDate(CalendarDay.from(2017, 0, 1))
            .setMaximumDate(CalendarDay.from(2030, 11, 31))
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()

        DataManager.getAllScheduleData()
        DataManager.dataList.observe(this, androidx.lifecycle.Observer {
            mScheduleList = it
            putDataOnCalendar()
        })


        calView.addDecorators(SundayDecorator(),
            SaturdayDecorator(),
            OneDayDecorator()
        )

        calView.setOnDateChangedListener(OnDateSelectedListener { widget, date, selected ->
            val intent = Intent(this, DayActivity::class.java)
            val day: CalendarDay = CalendarDay.from(date.year, date.month, date.day)
            Log.i("kongyi1220", "before send = " + map[day].toString())
            var schedule:Schedule? = map[day]
            if (schedule == null) { // if it is null, make dummy
                schedule = Schedule("${date.year}~${date.month}~${date.day}","${date.year}~${date.month}~${date.day}", "", "")
            }
            intent.putExtra("info", schedule)
            startActivity(intent);
        })

//        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
//        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        recyclerView.layoutManager = manager
//        recyclerView.adapter = DayListAdapter(mScheduleList!!)
    }

    private fun putDataOnCalendar() {
        val calView = findViewById<MaterialCalendarView>(R.id.calendarView)
        val dates = ArrayList<CalendarDay>()
        calView.removeDecorators()
        Log.i("kongyi1220", "mScheduleList.size = ${mScheduleList?.size}")
        for (schedule in mScheduleList!!) {
            Log.i("kongyi1220", "show schedule.date = " + schedule.date)
            val day: CalendarDay? = Utils.getDateFromStringToCal(schedule.date)
            if (day != null) {
                map[day] = schedule
                dates.add(day)
            }
        }

        calView.addDecorators(
            EventDecorator(Color.RED, dates, this)
        )

//            Log.i("kongyi1220", "size = " + scheduleList?.size)
//            for (schedule in scheduleList!!) {
//                Log.i("kongyi1220", "date = " + schedule.date)
//            }
    }


    override fun onStart() {
        super.onStart()

    }

}

class SundayDecorator : DayViewDecorator {
    private val calendar = Calendar.getInstance()
    override fun shouldDecorate(day: CalendarDay): Boolean {
        day.copyTo(calendar)
        val weekDay = calendar[Calendar.DAY_OF_WEEK]
        return weekDay == Calendar.SUNDAY
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(ForegroundColorSpan(Color.RED))
    }
}

class SaturdayDecorator : DayViewDecorator {
    private val calendar = Calendar.getInstance()
    override fun shouldDecorate(day: CalendarDay): Boolean {
        day.copyTo(calendar)
        val weekDay = calendar[Calendar.DAY_OF_WEEK]
        return weekDay == Calendar.SATURDAY
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(ForegroundColorSpan(Color.BLUE))
    }
}

class OneDayDecorator : DayViewDecorator {
    private var date: CalendarDay?
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return date != null && day == date
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(StyleSpan(Typeface.BOLD))
        view.addSpan(RelativeSizeSpan(1.4f))
        view.addSpan(ForegroundColorSpan(Color.GREEN))
    }

    /**
     * We're changing the internals, so make sure to call [MaterialCalendarView.invalidateDecorators]
     */
    fun setDate(date: Date?) {
        this.date = CalendarDay.from(date)
    }

    init {
        date = CalendarDay.today()
    }
}

class EventDecorator(color: Int, dates: Collection<CalendarDay>, context: Activity) :
    DayViewDecorator {
    var drawable: Drawable
    var color: Int
    private var dates: HashSet<CalendarDay>


    init {
        drawable = context.resources.getDrawable(R.drawable.ic_launcher_background)
        this.color = color
        this.dates = HashSet(dates)
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
//        view.setSelectionDrawable(drawable)
        val dot = DotSpan(5.0f, color)
        view.addSpan(dot); // 날자밑에 점
    }

}
