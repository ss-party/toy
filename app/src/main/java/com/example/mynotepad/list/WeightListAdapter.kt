package com.example.mynotepad.list

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotepad.R
import com.example.mynotepad.data.WeightPaperData

class WeightListAdapter(private val context: Context) : RecyclerView.Adapter<WeightListAdapter.ViewHolder>() {
    var datas = mutableListOf<WeightPaperData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_weight_recycler,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i("kongyi1220", "onBindViewHolder datas = ${datas.toString()}")
        holder.bind(datas[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val circleColor: ImageView = itemView.findViewById(R.id.paper_weight_color_circle)
        private val title: TextView = itemView.findViewById(R.id.paper_weight_item_title)
        private val mainText: TextView = itemView.findViewById(R.id.paper_weight_question_tv)
        private val subText: TextView = itemView.findViewById(R.id.paper_weight_sub_tv)

        fun bind(item: WeightPaperData) {
            Log.i("kongyi1220", "bind datas = ${datas.toString()}")

//            circleColor.text = item.paper_weight_color_circle
            title.text = item.paper_weight_item_title
            mainText.text = item.paper_weight_question_tv
            subText.text = item.paper_weight_sub_tv
        }
    }


}