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
import androidx.appcompat.app.AppCompatActivity
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.prolificinteractive.materialcalendarview.spans.DotSpan





class CalendarActivity : AppCompatActivity() {
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


        val dates = ArrayList<CalendarDay>()
        for (i in 1..5) {
            val day:CalendarDay = CalendarDay.from(2021,10,21)
            dates.add(day)
        }


        calView.addDecorators(SundayDecorator(),
            SaturdayDecorator(),
            OneDayDecorator(),
            EventDecorator(Color.RED, dates, this)
        )

        calView.setOnDateChangedListener(OnDateSelectedListener { widget, date, selected ->
            val intent = Intent(this, DayActivity::class.java)
            intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        })
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
        view.addSpan(DotSpan(5.0f, color)); // 날자밑에 점
    }

}
