package com.example.mynotepad

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.viewpager.widget.ViewPager
import java.util.*


class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "kongyi123/MainViewModel"
    var currentSheetId:Int = 0
    var currentTextView: TextView? = null
    var currentOrder: Int = 0
    var isFisrtStart = true
    var viewModel_LocalValue: String? = null
        private set
    var sheetCount: Int = 0
    var sheetIdCount:Int = 0
    var sheets: ArrayList<Sheet> = ArrayList<Sheet>()
    var sheetSelectionTab: LinearLayout? = null
    var currentContentTextSize:Float = 10.0f
    var adapterViewPager: FragmentPagerAdapter? = null
    val sheetOrder: MutableMap<View, Int> = mutableMapOf<View, Int>()
    var vpPager:ViewPager? = null

    fun getTextSizeById(id: Int):Float {
        for (i in 1..sheets!!.size) {
            if (sheets?.get(i-1)?.getId() == id) {
                return sheets?.get(i-1)?.getTextSize()!!
            }
        }
        return 10.0f
    }

    fun addNewSheet(context: Context, vpPager: ViewPager, switchFocusSheetInTab: (View) -> Unit, addShowingSheet: (TextView) -> Unit) {
        val textView = TabTextView(context);
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        textView.layoutParams = params
        textView.text = "newSheet"
        textView.id = ++sheetIdCount
//        textView.background = getDrawable(R.drawable.edge)
        textView.setBackgroundColor(context.resources.getColor(R.color.colorActivatedSheet))
//        textView.typeface = resources.getFont(R.font.whj000f0cb5)
        textView?.setOnClickListener {
            switchFocusSheetInTab(it)
            sheetOrder?.get(it as View)?.let { it1 -> // view - order match.
                vpPager.setCurrentItem(it1, true)
            }
        }
        sheets?.add(Sheet(sheetIdCount!!, "newSheet", "new", textView, 10.0f))
        currentContentTextSize = 10.0f
        addShowingSheet(textView)
//        adapterViewPager?.getItem()
        sheetOrder?.set(textView, sheetIdCount-1)
        adapterViewPager?.notifyDataSetChanged()
        switchFocusSheetInTab(textView)
        vpPager.setCurrentItem(sheetIdCount-1, true)
    }

    fun initViewPager(vpPager: ViewPager, supportFragmentManager: FragmentManager) {
        Log.d(TAG, "initViewPager")
        adapterViewPager = MyPagerAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        vpPager.adapter = adapterViewPager
    }

    var adapterSheetFragmentArray: ArrayList<SheetFragment> = ArrayList<SheetFragment>()


    inner class MyPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {
        override fun getCount(): Int {
            Log.d(TAG, "getCount")
            return sheets!!.size
        }
        // Returns the fragment to display for that page
        // 여기서 뷰를 처음 만든다. (일단 한 번 만들었으면, 호출이 되지 않음)
        override fun getItem(position: Int): Fragment {
            Log.d(TAG, "getItem")
            val sheetFragment = SheetFragment()
            sheetFragment.initialize(sheets[position].getContent()!!, sheets[position].getTextSize()!!)
            Log.d("kongyi123", " get Text size = " + sheets[position].getTextSize())
            adapterSheetFragmentArray.add(sheetFragment)
            return sheetFragment
        }

        // Returns the page title for the top indicator
        override fun getPageTitle(position: Int): CharSequence? {
            Log.d(TAG, "getPageTitle")
            return "Page $position"
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            Log.d(TAG, "instantiateItem")
            return super.instantiateItem(container, position)
        }

        override fun finishUpdate(container: ViewGroup) {
            Log.d(TAG, "finishUpdate")
            super.finishUpdate(container)
        }
    }
}