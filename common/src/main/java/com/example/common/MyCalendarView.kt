package com.example.common

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// reference
// https://doitddo.tistory.com/99

class MyCalendarView @JvmOverloads constructor(
    context: Context, attrs:AttributeSet? = null, defStyle: Int = 0
): FrameLayout(context) {

    private lateinit var mImg: ImageView
    private val IMAGEVIEW_TAG = "드래그 이미지"

    init {
        inflate(context, R.layout.my_calendar, this)
        initializeDragAndDropView()
        val calendarPager = findViewById<ViewPager2>(R.id.calendar_vpPager)
        val items: ArrayList<ArrayList<String>> = ArrayList()
        items.add(arrayListOf("일", "월", "화", "수", "목", "금", "토", "", "", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""))
        items.add(arrayListOf("일", "월", "화", "수", "목", "금", "토", "", "", "", "", "", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""))
        items.add(arrayListOf("일", "월", "화", "수", "목", "금", "토", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""))
        items.add(arrayListOf("일", "월", "화", "수", "목", "금", "토", "", "", "", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""))
        items.add(arrayListOf("일", "월", "화", "수", "목", "금", "토", "", "", "", "", "", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""))
        items.add(arrayListOf("일", "월", "화", "수", "목", "금", "토", "", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""))

        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.Main).launch {
                calendarPager.adapter = RecyclerViewAdapter(context, items)
                calendarPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
//        viewPager.adapter = FragmentStateAdapter()
            }
            delay(100)
            //val item = array.valueAt(0) as ConstraintLayout // 최초 순서에 있는 값 get
//            val item = array.get(10) as ConstraintLayout // 키 값에 대응되는 곳에 있는 값 get
//            val textView = item.getChildAt(0) as TextView
//            textView.text = "hi!"
        }

//        calendarPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//
//            }
//        })
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
                        if (cnt > 6 && cnt % 7 == 6) textView.setTextColor(
                            ContextCompat.getColor(
                                mContext,
                                R.color.teal_200
                            )
                        )
                        if (cnt > 6 && cnt % 7 == 0) textView.setTextColor(
                            ContextCompat.getColor(
                                mContext,
                                R.color.purple_200
                            )
                        )
                        cnt ++
                        textView.visibility = View.VISIBLE
                    }
                }
                //itemView.findViewById<TextView>(R.id.text).text = item
            }
        }
    }
}
