package com.example.mychartviewlibrary.calendar

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.common.ContextHolder
import com.example.model.data.Schedule
import com.example.mychartviewlibrary.R
import com.example.mychartviewlibrary.calendar.data.DateItem
import com.example.mychartviewlibrary.calendar.list.DayListAdapter
import java.util.*

// 뷰를 두번 열면 점의 개수가 두배가 됨.
// 오늘 날짜가 아니라 하루 전 날짜가 오늘 날짜로 보임

class MyCalendarView : FrameLayout {

    constructor(context: Context) : super(context){
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
    }

    private lateinit var mImg: ImageView
    private val IMAGEVIEW_TAG = "드래그 이미지"

    private lateinit var mRecyclerView: RecyclerView
    private val mMap = SparseArray<ArrayList<Schedule>>()

    private lateinit var mCalendarAdapter: RecyclerViewAdapterForCalendar

    init {
        ContextHolder.setContext(this.context)
        inflate(context, R.layout.my_calendar, this)
        initializeDragAndDropView()
        val titlePager = findViewById<ViewPager2>(R.id.calendar_title)
        val calendarPager = findViewById<ViewPager2>(R.id.calendar_vpPager)
        val calendarData: ArrayList<ArrayList<DateItem>> = ArrayList()
        val titleData: ArrayList<String> = ArrayList()
        var todayPosition = 0
        var weight = 1
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()

        for (year in 2022..2023) {
            for (month in 1..12) {
                todayPosition += weight
                if (cal.get(Calendar.YEAR) == year &&
                    cal.get(Calendar.MONTH) == month) {
                    weight = 0
                }
                val monthForCalendarLib = month-1
                calendarData.add(getMonthData(year, monthForCalendarLib))
                titleData.add("${month}월 $year")
            }
        }

        val onPageChangeCallbackForCalendar = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                titlePager.setCurrentItem(position, true)
            }
        }

        mCalendarAdapter = RecyclerViewAdapterForCalendar(context, calendarData, mMap)
        calendarPager.adapter = mCalendarAdapter
        calendarPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        calendarPager.registerOnPageChangeCallback(onPageChangeCallbackForCalendar)
        Log.i("kongyi0505", "mTodayPosition = ${todayPosition}")
        calendarPager.setCurrentItem(todayPosition, false)

        titlePager.adapter = RecyclerViewAdapterForTitle(titleData)
        titlePager.orientation = ViewPager2.ORIENTATION_VERTICAL
        titlePager.setCurrentItem(todayPosition, false)

        findViewById<TextView>(R.id.calendar_previous).setOnClickListener {
            val current = calendarPager.currentItem
            if (current > 0) {
                calendarPager.setCurrentItem(current-1, true)
            }
        }
        findViewById<TextView>(R.id.calendar_next).setOnClickListener {
            val current = calendarPager.currentItem
            if (current < calendarData.size) {
                calendarPager.setCurrentItem(current+1, true)
            }
        }

        findViewById<Button>(R.id.calendar_deleteAllBtn).setOnClickListener {
//            showDialog(date)
        }

//        findViewById<Button>(R.id.calendar_addScheduleBtn).setOnClickListener(addScheduleBtnListener)
//            // 새로 입력 하는 것으로 수정 필요
//            val intent = Intent(this, DayActivity::class.java)
//            val day: CalendarDay = CalendarDay.from(date.year, date.month, date.day)
//            val schedule = Schedule("no_id","${date.year}~${date.month}~${date.day}", "", "", "")
//            intent.putExtra("info", schedule)
//            startActivity(intent);

    }


    fun setOnItemClickListener(mScheduleList: ArrayList<Schedule>?) {
        // listener: OnDateItemClickListener
        mCalendarAdapter.listener = object : OnDateItemClickListener {
            override fun onItemClick(
                holder: RecyclerViewAdapterForCalendar.ViewHolder,
                view: View,
                position: Int,
                dateItem: DateItem
            ) {
                Log.i("kongyi0505", "here clicked.")
                val selectedDate = dateItem.year.toString() + "~" + dateItem.month.toString() + "~" + dateItem.date.toString()
                loadDataAtList(mScheduleList, selectedDate)
            }
        }
    }

    fun loadDataAtList(mScheduleList: ArrayList<Schedule>?, selectedDate:String) {
        Log.i("kongyi0503", "loadDataAtList()")
        val manager = LinearLayoutManager(ContextHolder.getContext(), LinearLayoutManager.VERTICAL, false)
        mRecyclerView = findViewById(R.id.calendar_recyclerView)
        mRecyclerView.layoutManager = manager
        mRecyclerView.adapter = DayListAdapter(mScheduleList!!, selectedDate)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun setSchedules(mScheduleList: ArrayList<Schedule>?) {
        mMap.clear()
        if (mScheduleList != null) {
            for (schedule in mScheduleList) {
                Log.i("kongyi0504", "schedule.title = ${schedule.title}")

                val day = Utils.getMyDateFromStringToCal(schedule.date)
                if (day != null) {
                    if (mMap[day.getKey()] == null) {
                        val list = ArrayList<Schedule>(1)
                        list.add(schedule)
                        mMap.append(day.getKey(), list)
                    } else {
//                        Log.i("kongyi0504", "${day.getKey()}")
                        val list = mMap[day.getKey()]
                        list.add(schedule)
                        mMap.append(day.getKey(), list)
                    }
                }
            }
        }
    }

    override fun onViewRemoved(child: View?) {
        super.onViewRemoved(child)
        ContextHolder.setContext(null)
    }
    // 뷰를 넣어야함...
    private fun getMonthData(year:Int, month:Int): ArrayList<DateItem> {
        val cal = Calendar.getInstance()
        cal.set(year, month, 1)
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val dayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dates = arrayListOf(
            DateItem(text = "일"), DateItem(text = "월"), DateItem(text = "화"), DateItem(text = "수"),
            DateItem(text = "목"), DateItem(text = "금"), DateItem(text = "토"))

        Log.i("kongyi0428", "dayOfWeek = $dayOfWeek")
        Log.i("kongyi0428", "dayOfMonth = $dayOfMonth")

        for (i in 1 until dayOfWeek) {
            dates.add(DateItem(text = ""))
        }
        var cnt = 0
        for (i in 1..dayOfMonth) {
            cnt++
            dates.add(DateItem(year, month, cnt, 1, cnt.toString()))
        }
        for (i in 0..30) {
            dates.add(DateItem(text = ""))
        }

        return dates
    }




    private fun initializeDragAndDropView() {
        mImg = findViewById(R.id.image)
        mImg.tag = IMAGEVIEW_TAG

        mImg.setOnLongClickListener {
            // 태그 생성
            Log.i("kongyi0424", "setOnLongClickListener")

            val shadowBuilder = DragShadowBuilder(it)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                it.startDragAndDrop(null, shadowBuilder, it, 0)
            }

            it.visibility = INVISIBLE
            true
        }

        findViewById<LinearLayout>(R.id.view1).setOnDragListener(DragListener())
        findViewById<LinearLayout>(R.id.view2).setOnDragListener(DragListener())
        findViewById<LinearLayout>(R.id.view3).setOnDragListener(DragListener())
    }

    inner class DragListener : OnDragListener {
        override fun onDrag(v: View, event: DragEvent): Boolean {
            // 이벤트 시작
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> { // 각 리스너의 드래그 앤 드롭 시작 상태 (3번 call됨)
                    Log.i("kongyi0424", "ACTION_DRAG_STARTED")
                }
                DragEvent.ACTION_DRAG_ENTERED -> { // 이미지가 들어옴
                    Log.i("kongyi0424", "ACTION_DRAG_ENTERED")
                }
                DragEvent.ACTION_DRAG_EXITED -> { // 이미지가 나감
                    Log.i("kongyi0424", "ACTION_DRAG_EXITED")
                }
                DragEvent.ACTION_DROP -> {
                    Log.i("kongyi0424", "ACTION_DROP")
                    if (v === findViewById<View>(R.id.view1) ||
                        v === findViewById<View>(R.id.view2) ||
                        v === findViewById<View>(R.id.view3)) {
                        val view = event.localState as View
                        (view.parent as ViewGroup).removeView(view)

                        val containView = v as LinearLayout
                        containView.addView(view)
                    }
                }
                DragEvent.ACTION_DRAG_ENDED -> { // 각 리스너의 드래그 앤 드롭 종료 상태 (3번 call됨)
                    Log.i("kongyi0424", "ACTION_DRAG_ENDED")
                    val view = event.localState as View
                    view.visibility = VISIBLE
                }
                else -> {
                }
            }
            return true
        }
    }
}
