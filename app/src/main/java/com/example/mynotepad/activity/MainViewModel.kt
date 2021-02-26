package com.example.mynotepad.activity

import android.app.Application
import android.app.Service
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.viewpager.widget.ViewPager
import com.example.mynotepad.R
import com.example.mynotepad.data.DataManager
import com.example.mynotepad.data.Sheet
import com.example.mynotepad.utility.PreferenceManager
import com.example.mynotepad.utility.SoftKeyboard
import com.example.mynotepad.view.SheetFragment
import com.example.mynotepad.view.TabTextView
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "kongyi123/MainViewModel"
    var currentSheetId:Int = 0
    var currentTabTitleView: TextView? = null
    var isFirstStart = true

    var sheetCount: Int = 0
    var sheetIdCount:Int = 0
    var sheets: ArrayList<Sheet> = ArrayList<Sheet>()
    var sheetSelectionTab: LinearLayout? = null
    var adapterViewPager: FragmentPagerAdapter? = null
    private val sheetOrder: MutableMap<View, Int> = mutableMapOf<View, Int>()
    var vpPager:ViewPager? = null
    var currentTabPosition:Int = 0
    private val dataManager = DataManager(getApplication())
    var softKeyboard: SoftKeyboard? = null
    var rootLayout: ViewGroup? = null
    var controlManager: InputMethodManager? = null

    fun getTextSizeById(id: Int):Float {
        for (i in 1..sheets!!.size) {
            if (sheets?.get(i-1)?.getId() == id) {
                return sheets?.get(i-1)?.getTextSize()!!
            }
        }
        return 10.0f
    }

    /** Initialize
     */
    fun initialize(context:Context, supportFragmentManager:FragmentManager):Boolean {
        sheetSelectionTab = (context as AppCompatActivity).findViewById(R.id.tabInner)
        loadSheetData()
        rootLayout = context.findViewById<LinearLayout>(R.id.root_layout)
        controlManager = context.getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager
        softKeyboard = SoftKeyboard(rootLayout, controlManager)
        softKeyboard!!.setSoftKeyboardCallback(object : SoftKeyboard.SoftKeyboardChanged {
            override fun onSoftKeyboardHide() {
                Log.d(TAG, "keyboard hided")
            }

            override fun onSoftKeyboardShow() {
                Log.d(TAG, "keyboard onSoftKeyboardShow")
            }
        })

        initViewPager(vpPager!!, supportFragmentManager)
        return true
    }

    fun updateFragmentToSheets() {
        for (i in 1..sheets.size) {
            if (sheets[i-1].getSheetFragment() != null) {
                sheets[i-1].setContent(sheets[i-1].getSheetFragment()?.editText?.text.toString())
                sheets[i-1].setTextSize(sheets[i-1].getSheetFragment()?.textSize!!)
            }
        }
    }

    /** Switch focused Sheet in Tab at the bottom line at the screen
     */
    fun switchFocusSheetInTab(position:Int) {
        switchFocusSheetInTab(sheets?.get(position)?.getTabTitleView() as View)
    }

    /** Save all of the data in the application.
     */
    fun saveAllIntoDB() {
        // 현재 프레그 먼트 덩어리에 있는 것을 저장하여 올림
        updateFragmentToSheets()

        for (i in 1..sheets!!.size) {
            val sheetNameKey = "sheetName$i"
            val sheetContentKey = "sheetContent$i"
            val sheetIdKey = "sheetId$i"
            val sheetTextSizeKey = "sheetTextSize$i"

            dataManager.setString(sheetNameKey, sheets?.get(i-1)?.getName())
            dataManager.setString(sheetContentKey, sheets?.get(i-1)?.getContent())
            dataManager.setString(sheetIdKey, sheets?.get(i-1)?.getId().toString())
            dataManager.setFloat(sheetTextSizeKey, sheets?.get(i-1)?.getTextSize()!!)
        }
        dataManager.setInt("sheetCount", sheets!!.size)
        dataManager.setInt("sheetIdCount", sheetIdCount!!)
        Toast.makeText(getApplication(), "saved", Toast.LENGTH_SHORT).show()
    }

    /** Load All of the Sheet data
     */
    private fun loadSheetData() {
        val context:Context = getApplication()
        isFirstStart = false
        sheetCount = dataManager.getInt("sheetCount")
        sheetIdCount = dataManager.getInt("sheetIdCount")
        if (sheetCount!! > 0) {
            for (i in 1..sheetCount!!) {
                val sheetNameKey = "sheetName$i"
                val sheetContentKey = "sheetContent$i"
                val sheetIdKey = "sheetId$i"
                val sheetTextSizeKey = "sheetTextSize$i"
                var sheetName = PreferenceManager.getString(context, sheetNameKey)
                var sheetContent = PreferenceManager.getString(context, sheetContentKey)
                var sheetId:String? = PreferenceManager.getString(context, sheetIdKey)
                var sheetTextSize:Float? = PreferenceManager.getFloat(context, sheetTextSizeKey)
                if (sheetTextSize == -1.0f) {
                    sheetTextSize = 10.0f
                }
                if (sheetId != null) {
                    val textView = TabTextView(context.applicationContext);
                    sheets?.add(Sheet(sheetId!!.toInt(), sheetName, sheetContent, textView, sheetTextSize))
                    val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    textView.layoutParams = params
                    textView.text = sheetName
                    textView.id = sheetId!!.toInt()
                    textView.setBackgroundColor(context.resources.getColor(R.color.colorDeactivatedSheet))
                    sheetOrder?.set(textView, i - 1)
                    textView.setOnClickListener {
                        switchFocusSheetInTab(it)
                        sheetOrder?.get(it as View)?.let { it1 ->
//                                Toast.makeText(this, "it1 = " + it1, Toast.LENGTH_SHORT).show()
                            vpPager?.setCurrentItem(it1, true)
                            currentTabPosition = sheetOrder?.get(it as View)!!
                        }
                    }
                    addShowingSheetInTab(textView)
                }
            }
            currentTabTitleView = sheets?.get(0)?.getTabTitleView()
            if (currentTabTitleView != null) {
                currentSheetId = currentTabTitleView!!.id
                currentTabTitleView!!.setBackgroundColor(context.resources.getColor(
                    R.color.colorActivatedSheet
                ))
            }
        } else {
            Toast.makeText(context, "저장된 데이터가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    /** Switch focus sheet in bottom tab
     * - change currentSheetId, currentTabTextView
     */
    private fun switchFocusSheetInTab(it:View) {
        val context:Context = getApplication()
        Log.d(TAG, "switchFocusSheetInTab")

        val view = it as TextView
        if (view.id == currentSheetId) {
            return
        }
        val existTextView = currentTabTitleView
        currentSheetId = view.id
        currentTabTitleView = view
        existTextView?.setBackgroundColor(context.resources.getColor(R.color.colorDeactivatedSheet))
        view.setBackgroundColor(context.resources.getColor(R.color.colorActivatedSheet))
        Log.d(TAG,"id = " + currentSheetId + " / sheet id count = " + sheetIdCount!! )
//        Toast.makeText(this, "id = " + viewModel?.currentSheetId + " / sheet id count = " + viewModel?.sheetIdCount!!, Toast.LENGTH_SHORT).show()
    }

    /** Add new title view of a sheet into the tab bottom side of screen
     * - If there is previous parent (linear layout) of view, it should be called removeView method.
     */
    private fun addShowingSheetInTab(view: View) {
        if (view.parent != null) {
            ((view.parent) as ViewGroup).removeView(view)
        }
        sheetSelectionTab?.addView(view)
    }

    /** Increase text size of the text content in current text screen
     */
    fun contentTextSizeIncrease() {
        val currentContentTextSize = sheets!![currentTabPosition!!].getTextSize()!! + 1
        sheets!![currentTabPosition!!].setTextSize(currentContentTextSize)
    }

    /** Decrease text size of the text content in current text screen
     */
    fun contentTextSizeDecrease() {
        val currentContentTextSize = sheets!![currentTabPosition!!].getTextSize()!! - 1
        sheets!![currentTabPosition!!].setTextSize(currentContentTextSize)
    }

//    fun addNewSheet(context: Context, vpPager: ViewPager, switchFocusSheetInTab: (View) -> Unit, addShowingSheet: (TextView) -> Unit) {
    fun addNewSheet(vpPager: ViewPager) {
        val context:Context = getApplication()

        val textView = TabTextView(context)
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
        addShowingSheetInTab(textView)
//        adapterViewPager?.getItem()
        sheetOrder?.set(textView, sheetIdCount-1)
        adapterViewPager?.notifyDataSetChanged()
        switchFocusSheetInTab(textView)
        vpPager.setCurrentItem(sheetIdCount-1, true)
    }

    private fun initViewPager(vpPager: ViewPager, supportFragmentManager: FragmentManager) {
        Log.d(TAG, "initViewPager")
        adapterViewPager = MyPagerAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        vpPager.adapter = adapterViewPager
    }

    inner class MyPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {
        override fun getCount(): Int {
            return sheets!!.size
        }
        // Returns the fragment to display for that page
        // 여기서 뷰를 처음 만든다. (일단 한 번 만들었으면, 호출이 되지 않음)
        override fun getItem(position: Int): Fragment {
            Log.d(TAG, "getItem")
            val sheetFragment = SheetFragment()
            sheetFragment.initialize(sheets[position].getContent()!!, sheets[position].getTextSize()!!, position)
            sheets[position].setSheetFragment(sheetFragment)
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

    override fun onCleared() {
        super.onCleared()
        softKeyboard?.unRegisterSoftKeyboardCallback();
    }
}