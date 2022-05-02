package com.example.mychartviewlibrary.calendar

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.common.ContextHolder
import com.example.mychartviewlibrary.R
import com.example.mychartviewlibrary.calendar.data.DateItem
import com.example.mychartviewlibrary.calendar.list.DayListAdapter
import java.util.*


class MyCalendarView @JvmOverloads constructor(
    context: Context, attrs:AttributeSet? = null, defStyle: Int = 0
): FrameLayout(context) {

    private lateinit var mImg: ImageView
    private val IMAGEVIEW_TAG = "드래그 이미지"

    private var rvAdapter: DayListAdapter? = null
    private lateinit var mRecyclerView: RecyclerView
    private var mSelectedDate:Calendar? = null

    init {
        ContextHolder.setContext(this.context)
        inflate(context, R.layout.my_calendar, this)
        initializeDragAndDropView()
        val titlePager = findViewById<ViewPager2>(R.id.calendar_title)
        val calendarPager = findViewById<ViewPager2>(R.id.calendar_vpPager)
        val calendarData: ArrayList<ArrayList<DateItem>> = ArrayList()
        val titleData: ArrayList<String> = ArrayList()
        for (year in 2022..2023) {
            for (month in 1..12) {
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

        calendarPager.adapter = RecyclerViewAdapterForCalendar(context, calendarData)
        calendarPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        calendarPager.registerOnPageChangeCallback(onPageChangeCallbackForCalendar)

        titlePager.adapter = RecyclerViewAdapterForTitle(titleData)
        titlePager.orientation = ViewPager2.ORIENTATION_VERTICAL


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

        mRecyclerView = findViewById(R.id.calendar_recyclerView)

//        rvAdapter = DayListAdapter(
//            mScheduleList!!,
//            date.year.toString() + "~" + date.month.toString() + "~" + date.day.toString()
//        )
//        mRecyclerView.layoutManager = manager
        mRecyclerView.adapter = rvAdapter
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
