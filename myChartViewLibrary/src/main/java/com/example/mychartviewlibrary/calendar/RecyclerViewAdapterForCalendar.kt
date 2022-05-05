package com.example.mychartviewlibrary.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.util.SparseArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.model.data.Schedule
import com.example.mychartviewlibrary.R
import com.example.mychartviewlibrary.calendar.data.DateItem
import java.util.*

// --> 리사이클러뷰의 재활용성 때문에, 화면에 표시하는 뷰를 다 갖고 있으려고 하면 안된다.
class RecyclerViewAdapterForCalendar(private val context: Context,
                                     private var items: ArrayList<ArrayList<DateItem>>,
                                     val map:SparseArray<ArrayList<Schedule>>) : RecyclerView.Adapter<RecyclerViewAdapterForCalendar.ViewHolder>() {
    val mContext = context
    val getTextViewByKey = SparseArray<TextView>()
    var mSelectedView:View? = null
    lateinit var listener: OnDateItemClickListener

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
        holder.setData(position, items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun setData(pos:Int, arr: ArrayList<DateItem>) {
            val frame = itemView.rootView as LinearLayout
            val frameChildCount = frame.childCount
            var cnt = 0
            for (i in 0 until frameChildCount) {
                val row = frame.getChildAt(i) as LinearLayout
                val rowChildCount = row.childCount
                for (j in 0 until rowChildCount) {
                    val item = row.getChildAt(j) as LinearLayout
                    //item.background = getDrawable(mContext, R.drawable.ic_launcher_background)
                    val view = arr[cnt].view as ConstraintLayout
                    val textView = view.getChildAt(0) as TextView
                    val linearLayout = view.getChildAt(1) as LinearLayout
                    textView.text = arr[cnt].text
                    view.layoutParams = ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT)
                    textView.gravity = Gravity.CENTER
                    item.removeAllViews()
                    setScheduleAtDate(arr, cnt, linearLayout)
                    item.addView(view)
                    if (cnt > 6 && cnt % 7 == 6)
                        textView.setTextColor(ContextCompat.getColor(mContext, R.color.blue))
                    if (cnt > 6 && cnt % 7 == 0)
                        textView.setTextColor(ContextCompat.getColor(mContext, R.color.red))
                    textView.visibility = View.VISIBLE
                    getTextViewByKey.append(arr[cnt].getKey(), textView)

                    markToday(arr, cnt, textView)

                    if (arr[cnt].role == 1) {
                        val value = arr[cnt]
                        item.getChildAt(0).setOnClickListener {
                            val previousView = mSelectedView
                            if (previousView != null) {
                                previousView.background = null
                            }
                            mSelectedView = it
                            val position = adapterPosition
                            if (listener != null) {
                                Log.i("kongyi0505", "arr[cnt] = [${value}]")
                                listener.onItemClick(this, it, position, value)
                            }
                            it.background = mContext.getDrawable(R.drawable.circle_light_yellow)
                        }
                    }
                    cnt ++
                }
            }
            //itemView.findViewById<TextView>(R.id.text).text = item
        }
    }

    private fun markToday(arr: ArrayList<DateItem>,
                          cnt: Int,
                          textView: TextView) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        if (cal.get(Calendar.YEAR) == arr[cnt].year &&
                cal.get(Calendar.MONTH) == arr[cnt].month &&
                cal.get(Calendar.DATE) == arr[cnt].date) {
            Log.i("kongyi0504", "markToday")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextColor(context.getColor(R.color.green_today))
                textView.textSize = 20.0f
//                textView.setBackgroundColor(context.getColor(R.color.green_today))
            }
        }
    }

    // 한 날짜에 같은 스케쥴이 두개 씩 들어가는 이슈 있음.
    private fun setScheduleAtDate(
        arr: ArrayList<DateItem>,
        cnt: Int,
        linearLayout: LinearLayout
    ) {
        val list = map[arr[cnt].getKey()]
        if (list != null) {
            linearLayout.removeAllViews()
            Log.i("kongyi0504", "list = {$list}")
            for (schedule in list) {
                when {
                    schedule.color == "blue" -> {
                        val img = ImageView(context)
                        img.background = context.getDrawable(R.drawable.circle_blue)
                        img.layoutParams = LinearLayout.LayoutParams(Utils.convertDPtoPX(context, 5), Utils.convertDPtoPX(context, 5))
                        linearLayout.addView(img)
                    }
                    schedule.color == "purple" -> {
                        val img = ImageView(context)
                        img.background = context.getDrawable(R.drawable.circle_purple)
                        img.layoutParams = LinearLayout.LayoutParams(Utils.convertDPtoPX(context, 5), Utils.convertDPtoPX(context, 5))
                        linearLayout.addView(img)
                    }
                    schedule.color == "green" -> {
                        val img = ImageView(context)
                        img.background = context.getDrawable(R.drawable.circle_green)
                        img.layoutParams = LinearLayout.LayoutParams(Utils.convertDPtoPX(context, 5), Utils.convertDPtoPX(context, 5))
                        linearLayout.addView(img)
                    }
                    schedule.color == "black" -> {
                        val img = ImageView(context)
                        img.background = context.getDrawable(R.drawable.circle_black)
                        img.layoutParams = LinearLayout.LayoutParams(Utils.convertDPtoPX(context, 5), Utils.convertDPtoPX(context, 5))
                        linearLayout.addView(img)
                    }
                    schedule.color == "orange" -> {
                        val img = ImageView(context)
                        img.background = context.getDrawable(R.drawable.circle_orange)
                        img.layoutParams = LinearLayout.LayoutParams(Utils.convertDPtoPX(context, 5), Utils.convertDPtoPX(context, 5))
                        linearLayout.addView(img)
                    }
                    schedule.color == "yellow" -> {
                        val img = ImageView(context)
                        img.background = context.getDrawable(R.drawable.circle_yellow)
                        img.layoutParams = LinearLayout.LayoutParams(Utils.convertDPtoPX(context, 5), Utils.convertDPtoPX(context, 5))
                        linearLayout.addView(img)
                    }
                    schedule.color == "red" -> {
                        val img = ImageView(context)
                        img.background = context.getDrawable(R.drawable.circle_red)
                        img.layoutParams = LinearLayout.LayoutParams(Utils.convertDPtoPX(context, 5), Utils.convertDPtoPX(context, 5))
                        linearLayout.addView(img)
                    }
                }
            }
        }
    }
}

