package com.example.mychartviewlibrary.calendar

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.mychartviewlibrary.R
import com.example.mychartviewlibrary.calendar.list.DayListAdapter
import java.util.*


class MyCalendarView @JvmOverloads constructor(
    context: Context, attrs:AttributeSet? = null, defStyle: Int = 0
): FrameLayout(context) {

    private lateinit var mImg: ImageView
    private val IMAGEVIEW_TAG = "드래그 이미지"

    private var rvAdapter: DayListAdapter? = null
    private lateinit var mRecyclerView: RecyclerView

    init {
        inflate(context, R.layout.my_calendar, this)
        initializeDragAndDropView()
        val titlePager = findViewById<ViewPager2>(R.id.calendar_title)
        val calendarPager = findViewById<ViewPager2>(R.id.calendar_vpPager)
        val calendarData: ArrayList<ArrayList<String>> = ArrayList()
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

        calendarPager.adapter = RecyclerViewAdapter(context, calendarData)
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

    private fun getMonthData(year:Int, month:Int): ArrayList<String> {
        val cal = Calendar.getInstance()
        cal.set(year, month, 1)
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val dayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dates = arrayListOf("일", "월", "화", "수", "목", "금", "토")

        Log.i("kongyi0428", "dayOfWeek = $dayOfWeek")
        Log.i("kongyi0428", "dayOfMonth = $dayOfMonth")

        for (i in 1 until dayOfWeek) {
            dates.add("")
        }
        var cnt = 0
        for (i in 1..dayOfMonth) {
            cnt++
            dates.add(cnt.toString())
        }
        for (i in 0..30) {
            dates.add("")
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


    // --> 리사이클러뷰의 재활용성 때문에, 화면에 표시하는 뷰를 다 갖고 있으려고 하면 안된다.
    class RecyclerViewAdapter(private val context: Context, private var items: ArrayList<ArrayList<String>>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
        val mContext = context
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            // 뷰홀더는 데이터의 개수만큼 만들어지는게 아니라.
            // 몇개만 만들어지고 재활용된다.

            Log.i("kongyi1220", "onCreateViewHolder")
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.calendar_list_item, parent, false))
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // 이 함수 화면을 보일 때, 항상 call 되기때문에
            // 여기서 데이터를 씌어 주어야 한다. 그래야 재활용 되는 뷰 홀더에 적절한 값이 표시된다.

            Log.i("kongyi1220", "onBindViewHolder")
            holder.setData(items[position])
        }
        override fun getItemCount(): Int = items.size
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun setData(arr: ArrayList<String>) {
                val frame = itemView.rootView as LinearLayout
                val frameChildCount = frame.childCount
                var cnt = 0
                for (i in 0 until frameChildCount) {
                    val row = frame.getChildAt(i) as LinearLayout
                    val rowChildCount = row.childCount
                    for (j in 0 until rowChildCount) {
                        val item = row.getChildAt(j) as ConstraintLayout
                        //item.background = getDrawable(mContext, R.drawable.ic_launcher_background)
                        val textView = item.getChildAt(0) as TextView
                        textView.text = "${arr[cnt]}"
                        if (cnt > 6 && cnt % 7 == 6)
                            textView.setTextColor(ContextCompat.getColor(mContext, R.color.blue))
                        if (cnt > 6 && cnt % 7 == 0)
                            textView.setTextColor(ContextCompat.getColor(mContext, R.color.red))
                        cnt ++
                        textView.visibility = View.VISIBLE
                    }
                }
                //itemView.findViewById<TextView>(R.id.text).text = item
            }
        }
    }

    class RecyclerViewAdapterForTitle(private var items: ArrayList<String>) : RecyclerView.Adapter<RecyclerViewAdapterForTitle.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.calendar_title, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.setData(items[position])
        }
        override fun getItemCount(): Int = items.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun setData(title: String) {
                (itemView as TextView).text = title
            }
        }

    }
}
