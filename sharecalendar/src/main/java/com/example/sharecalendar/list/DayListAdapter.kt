package com.example.sharecalendar.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sharecalendar.R
import com.example.sharecalendar.data.Schedule
import java.util.ArrayList

class DayListAdapter(mScheduleList: ArrayList<Schedule>) : RecyclerView.Adapter<ViewHolder>() {
    private var dayScheduleList: ArrayList<Schedule>? = null

    init {
        dayScheduleList = mScheduleList
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val context = parent.context
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = dayScheduleList!![position].title
        holder.content.text = dayScheduleList!![position].content
    }

    override fun getItemCount(): Int {
        return dayScheduleList!!.size
    }
}