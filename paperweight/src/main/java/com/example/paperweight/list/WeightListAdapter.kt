package com.example.paperweight.list

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.paperweight.R

class WeightListAdapter(private val context: Context) : RecyclerView.Adapter<WeightListAdapter.ViewHolder>() {
    var datas = mutableListOf<com.example.paperweight.WeightPaperData>()
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
        private val commentText: TextView = itemView.findViewById(R.id.paper_weight_comment)

        fun bind(item: com.example.paperweight.WeightPaperData) {
            Log.i("kongyi1220", "bind datas = ${datas.toString()}")

//            circleColor.text = item.paper_weight_color_circle
            val circleResource = when(item.paper_weight_color_circle) {
                "green" -> R.drawable.circle_green
                "blue" -> R.drawable.circle_blue
                "purple" -> R.drawable.circle_purple
                "red" -> R.drawable.circle_red
                "yellow" -> R.drawable.circle_yellow
                else -> R.drawable.circle_black
            }

            if (item.isYes) {
                itemView.findViewById<RadioButton>(R.id.paper_weight_radio_button_yes).isChecked = true
                itemView.findViewById<RadioButton>(R.id.paper_weight_radio_button_no).isChecked = false
            } else {
                itemView.findViewById<RadioButton>(R.id.paper_weight_radio_button_yes).isChecked = false
                itemView.findViewById<RadioButton>(R.id.paper_weight_radio_button_no).isChecked = true
            }

            itemView.findViewById<RadioButton>(R.id.paper_weight_radio_button_yes).setOnCheckedChangeListener { compoundButton, b ->
                Log.i("kongyi666", "item.isYes = ${item.isYes} compoundButton?.id = ${compoundButton?.id}")
                val pos = adapterPosition
                when(compoundButton?.id) {
                    R.id.paper_weight_radio_button_yes -> {
                        datas[pos].isYes = b
                    }
                    else -> {
                        Log.i("kongyi666", "error")
                    }
                }
            }

            itemView.findViewById<EditText>(R.id.paper_weight_comment).addTextChangedListener {
                Log.i("kongyi666", "content = ${it.toString()}")
                val pos = adapterPosition
                datas[pos].paper_weight_comment = it.toString()
            }

            circleColor.background = ContextCompat.getDrawable(context, circleResource)
            title.text = item.paper_weight_item_title
            mainText.text = item.paper_weight_question_tv
            subText.text = item.paper_weight_sub_tv
            commentText.text = item.paper_weight_comment
        }
    }


}