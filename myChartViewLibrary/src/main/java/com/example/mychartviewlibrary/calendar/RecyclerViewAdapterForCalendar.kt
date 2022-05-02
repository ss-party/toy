package com.example.mychartviewlibrary.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.util.SparseArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mychartviewlibrary.R
import com.example.mychartviewlibrary.calendar.data.DateItem
import java.util.*

// --> 리사이클러뷰의 재활용성 때문에, 화면에 표시하는 뷰를 다 갖고 있으려고 하면 안된다.
class RecyclerViewAdapterForCalendar(private val context: Context, private var items: ArrayList<ArrayList<DateItem>>) : RecyclerView.Adapter<RecyclerViewAdapterForCalendar.ViewHolder>() {
    val mContext = context
    val getTextViewByKey = SparseArray<TextView>()
    var mSelectedView:View? = null
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
                    val textView = arr[cnt].view as TextView
                    textView.text = arr[cnt].text
                    textView.layoutParams = LinearLayout.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT)
                    textView.gravity = Gravity.CENTER
                    item.removeAllViews()
                    item.addView(textView)
                    if (cnt > 6 && cnt % 7 == 6)
                        textView.setTextColor(ContextCompat.getColor(mContext, R.color.blue))
                    if (cnt > 6 && cnt % 7 == 0)
                        textView.setTextColor(ContextCompat.getColor(mContext, R.color.red))
                    cnt ++
                    textView.visibility = View.VISIBLE
                    getTextViewByKey.append(arr[cnt].getKey(), textView)
                    if (arr[cnt].role == 1) {
                        item.getChildAt(0).setOnClickListener {
                            val previousView = mSelectedView
                            if (previousView != null) {
                                previousView.background = null
                            }
                            mSelectedView = it
                            it.background = mContext.getDrawable(R.drawable.circle_light_yellow)
                        }
                    }
                }
            }
            //itemView.findViewById<TextView>(R.id.text).text = item
        }
    }
}

