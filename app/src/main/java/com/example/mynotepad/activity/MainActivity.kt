package com.example.mynotepad.activity

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.mynotepad.R
import com.example.mynotepad.utility.PreferenceManager
import com.example.mynotepad.utility.TTSpeech
import com.example.mynotepad.view.SheetFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog.view.*

class MainActivity : AppCompatActivity() {
    private val TAG = "kongyi123/MainActivity"
    private var items: MainViewModel? = null
    private var tts: TTSpeech? = null

    // option setting
    private val CLEAR = false

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        items = ViewModelProvider(this)[MainViewModel::class.java]
        items?.vpPager = findViewById(R.id.vpPager)
        tts = TTSpeech(this)

        clearData()

        if (items?.isFirstStart == true) {
            initializeDataForTheFirstTime()
        }

        tts?.initTTS()
        if (items?.sheetIdCount == 0) {
            items?.sheetIdCount = 15
        }

        items?.vpPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                //                Toast.makeText(ontext, "pos = " + position + " / viewModelSheetIdCount = " + viewModel?.sheetIdCount, Toast.LENGTH_SHORT).show()
                if (items?.size!! > 0 && position >= 0 && position < items?.sheetIdCount!!) {
                    Log.d(TAG, "registerOnPageChangeCallback : position = " + position)
                    switchFocusSheetInTab(position)
                    //(viewModel?.adapterViewPager as MainViewModel.MyPagerAdapter).adapterSheetFragmentArray[position].textSize = viewModel!!.sheets[position].getTextSize()
                }
                items?.currentTabPosition = position
            }
        })
        initializeHotKey()
    }

    private fun createViewPagerAdapter(): RecyclerView.Adapter<*> {
        val items = items
        return object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): SheetFragment {
                val sheetFragment = SheetFragment()
                sheetFragment.initialize(items!!.items[position]?.getContent()!!, items!!.items[position].getTextSize()!!, position)
                items!!.items[position].setSheetFragment(sheetFragment)
                return sheetFragment
            }

            override fun getItemCount(): Int = items!!.size
            override fun getItemId(position: Int): Long = items!!.itemId(position)
            override fun containsItem(itemId: Long): Boolean = items!!.contains(itemId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.d(TAG, "onCreateOptionsMenu")
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected")
        when(item.itemId) {
            R.id.menuSaveOptionBtn-> saveAllIntoDB()
            R.id.menuEditSheetNameBtn-> makeDialogAndEditSheetName()
            R.id.menuDeleteSheetBtn-> deleteCurrentSheet()
            R.id.menuTextSizeIncreaseBtn-> items?.contentTextSizeIncrease()
            R.id.menuTextSizeDecreaseBtn-> items?.contentTextSizeDecrease()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isTargetLastElement(target: Int):Boolean {
        return target == items?.sheetSize!!-1
    }

    private fun deleteCurrentSheet() {
        if (items!!.size <= 0) return
        for (i in 0 until items!!.size) {
            val textViewId: Int = items?.items?.get(i)?.getId()!!
            val order = items?.sheetOrder?.get(textViewId)
            if (order != null && order >= items?.currentTabPosition!!) {
                items?.sheetOrder?.set(textViewId, order-1)
            }
        }
        val target = items?.currentTabPosition!!
        val idsOld = items?.createIdSnapshot()
        items?.removeAt(target)
        val idsNew = items?.createIdSnapshot()
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = idsOld!!.size
            override fun getNewListSize(): Int = idsNew!!.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                idsOld!![oldItemPosition] == idsNew!![newItemPosition]
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                areItemsTheSame(oldItemPosition, newItemPosition)
        }, true).dispatchUpdatesTo(vpPager.adapter!!)

        items?.removeShowingSheetInTab(items?.currentTabTitleView as View)
        if (isTargetLastElement(target)) {
            if (items?.sheetSize!! > 1) {
                switchFocusSheetInTab(target - 1)
            }
        } else {
            switchFocusSheetInTab(target)
        }
        items?.sheetSize = items?.sheetSize!!-1

// 아래는 사장된 코드임... 처음 모르는 것을 배울때는, 예제를 그대로 따르는 것을 원칙으로 하자.
        // 아래 방식은 중간에 있는 항목에는 동작하지만, 마지막 끝 항에 대해서는 current page가 부적절하게 표시되는 오류가 있다.
        // 지금 보니 아래 방식도 구현 가능한 예제가 구글에서 제공하는 git에 제시되어있음.
//            items?.currentTabPosition = target-1
//        }
//        else {
//            Log.d(TAG, "currentTabPosition = " + target)
//            items?.removeAt(target)
//            vpPager.adapter!!.notifyDataSetChanged()
//            items?.removeShowingSheetInTab(items?.currentTabTitleView as View)
//            switchFocusSheetInTab(target)
//            items?.sheetSize = items?.sheetSize!!-1
//        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        saveAllIntoDB()
    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.close()
    }

    private fun switchFocusSheetInTab(position: Int) {
        items?.switchFocusSheetInTab(position)
        items?.updateFragmentToSheets()
        tabOuter.requestFocus()
        showAllData("switchFocusSheetInTab")
    }

    private fun makeDialogAndEditSheetName() {
        // make Dialog
        val dlg: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        val ad: AlertDialog = dlg.create()
        ad.setTitle("Edit Name") //제목
        val inflater: LayoutInflater = LayoutInflater.from(applicationContext)
        val view = inflater.inflate(R.layout.dialog, root_layout, false)
        ad.setView(view) // 메시지
        // set New Sheet Name if the confirm button is clicked.
        view.dialogConfirmBtn.setOnClickListener {
            for (i in 1..items?.items!!.size) {
                if (items?.currentTabTitleView?.id == items?.items?.get(i - 1)?.getId()) {
                    items?.items?.get(i - 1)?.getTabTitleView()?.text = view.dialogEditBox.text
                    items?.items?.get(i - 1)?.setName(view.dialogEditBox.text.toString())
                    break
                }
            }
            ad.dismiss()
        }
        ad.show()
    }

    fun onClickPlusIcon(view: View) {
        items?.addNewSheet(items?.vpPager!!)
    }

    private fun findInput() {

    }

    private fun findNext() {

    }

    private fun initializeHotKey() {
        Log.d(TAG, "initializeHotKey")

/*        val editText: EditText? = findViewById(R.id.editText)
        editText?.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_F -> if (event?.isCtrlPressed == true) {
                        Log.d("kongyi123", "ctrl + f pressed")
                        Toast.makeText(v?.context, "ctrl + f pressed", Toast.LENGTH_SHORT).show()
                        findInput()
                        //editText.setSelection(1,5)
                    }
                    KeyEvent.KEYCODE_F3 -> {
                        Toast.makeText(v?.context, "F3 pressed", Toast.LENGTH_SHORT).show()
                        findNext();
                    }
                    KeyEvent.KEYCODE_F2 -> {
                        Toast.makeText(v?.context, "F2 saved", Toast.LENGTH_SHORT).show()
                        save();
                    }
                    KeyEvent.KEYCODE_PLUS -> if (event?.isShiftPressed == true) {
                        Toast.makeText(v?.context, "plus pressed", Toast.LENGTH_SHORT).show()
                        viewModel?.currentTextSize = viewModel?.currentTextSize!! + 1;
                        editText.textSize  = viewModel?.currentTextSize!!
                    }
                    KeyEvent.KEYCODE_MINUS -> if (event?.isShiftPressed == true) {
                        Toast.makeText(v?.context, "minus pressed", Toast.LENGTH_SHORT).show()
                        viewModel?.currentTextSize = viewModel?.currentTextSize!! - 1;
                        editText.textSize = viewModel?.currentTextSize!!
                    }

                    KeyEvent.KEYCODE_F5 -> {
                        tts?.input(editText.text.toString())
                        tts?.start()
                    }
                    KeyEvent.KEYCODE_PAGE_UP -> if (event?.isCtrlPressed == true) {
                        if (tts != null && tts!!.speed < 3) {
                            tts!!.speed += 0.25f;
                            Toast.makeText(v?.context, "speed = " + tts!!.speed, Toast.LENGTH_SHORT).show()
                            tts?.setPitch()
                            tts?.setSpeechRate()
                        }
                    }
                    KeyEvent.KEYCODE_PAGE_DOWN -> if (event?.isCtrlPressed == true) {
                        if (tts != null && tts!!.speed > 1) {
                            tts!!.speed -= 0.25f;
                            Toast.makeText(v?.context, "speed = " + tts!!.speed, Toast.LENGTH_SHORT).show()
                            tts?.setPitch()
                            tts?.setSpeechRate()
                        }
                    }
                }
                true
            }
            false
        }
    */
    }

    /** Save data (the contents of sheets and so on)
     * 1. Move all data from adapterSheetFragmentArray to Sheets
     * 2. Set all data of sheets to PreferenceManager
     */
    private fun saveAllIntoDB() {
        Log.d(TAG, "save")
        items?.saveAllIntoDB()
    }

    /** Clear All sheets and data
     * - It works when CELAR variable set to true in code
     */
    private fun clearData() {
        if (CLEAR) {
            PreferenceManager.setInt(this, "sheetCount", 0)
            PreferenceManager.setInt(this, "sheetIdCount", 0)
            Toast.makeText(this, "초기화 완료", Toast.LENGTH_SHORT).show()
            return
        }
    }

    /** Get datas from PreferenceManager
    * - e.g. sheetCount, sheetIdCount, Sheets, currentTextView
    */
    private fun initializeDataForTheFirstTime() {
        Log.d(TAG, "initializeDataForTheFirstTime")
        val result = items?.initialize(this, supportFragmentManager)
        if (result == false) {
            Toast.makeText(this, "데이터 초기화 실패", Toast.LENGTH_SHORT).show()
        }
        vpPager.adapter = createViewPagerAdapter()
    }

    private fun showAllData(callingFunction: String) {
        Log.d(TAG, "showAllData calling fucntion = " + callingFunction)
        Log.d(TAG, "currentViewInTab = " + items?.currentTabTitleView!!.text)
        for (i in 1..items?.items!!.size) {
            val text: String = items?.items!![i-1].getContent().toString()
            val title: String = items!!.items[i-1]!!.getName()!!
            val viewId: Int = items!!.items[i-1]!!.getTabTitleView()!!.id
            val sheetId: Int = items!!.items[i-1]!!.getId()!!
            var length = text.length-1
            if (text.length >= 5) {
                length = 5
            }
            Log.d(TAG, "num = $i, content = ${text.substring(0, length)}, title = $title, viewId = $viewId sheetId = $sheetId")
        }
    }

}
