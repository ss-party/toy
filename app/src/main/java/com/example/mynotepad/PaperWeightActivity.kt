package com.example.mynotepad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotepad.data.WeightPaperData
import com.example.mynotepad.list.WeightListAdapter

class PaperWeightActivity : AppCompatActivity() {
    lateinit var weightListAdapter:WeightListAdapter
    val list = mutableListOf<WeightPaperData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paper_weight)

        initRecycler()
    }

    private fun initRecycler() {
        weightListAdapter = WeightListAdapter(this)
        findViewById<RecyclerView>(R.id.paper_recyclerview).adapter = weightListAdapter


        list.apply {
            Log.i("kongyi1220", "datas.apply applied")
            add(WeightPaperData("green", paper_weight_item_title = "기록", paper_weight_question_tv = "오늘 한 일 기록하자", paper_weight_sub_tv = "* 3가지 키워드만 쓰자", isYes = false))
            add(WeightPaperData("blue", paper_weight_item_title = "업무/익스", paper_weight_question_tv = "한 업무를 기록하자", paper_weight_sub_tv = "* 키워드-진행위치 형식으로", isYes = false))
            add(WeightPaperData("purple", paper_weight_item_title = "골프", paper_weight_question_tv = "골프 쳤니?", paper_weight_sub_tv = "* 깨달은 점도 비고란에", isYes = false))
            add(WeightPaperData("yellow", paper_weight_item_title = "재택근무", paper_weight_question_tv = "재택근무했니?", paper_weight_sub_tv = "", isYes = false))
//            add(WeightPaperData(img = R.drawable.profile3, name = "jenny", age = 26))
//            add(WeightPaperData(img = R.drawable.profile2, name = "jhon", age = 27))
//            add(WeightPaperData(img = R.drawable.profile5, name = "ruby", age = 21))
//            add(WeightPaperData(img = R.drawable.profile4, name = "yuna", age = 23))
            Log.i("kongyi1220", "datas.apply applied / datas = ${list.toString()}")

            weightListAdapter.datas = list
            weightListAdapter.notifyDataSetChanged()
        }
    }

}