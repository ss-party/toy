package com.example.mynotepad

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.mynotepad.Utils.PreferenceManager
import com.example.mynotepad.Utils.TTSpeech
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog.view.*

class MainActivity : AppCompatActivity() {
    private val TAG = "kongyi123/MainActivity"
    private var viewModel: MainViewModel? = null
    private var tts: TTSpeech? = null

    // option setting
    private val CLEAR = false

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel?.vpPager = findViewById<ViewPager>(R.id.vpPager)
        tts = TTSpeech(applicationContext)

        clearData()

        if (viewModel?.isFisrtStart == true) {
            initializeDataForTheFirstTime()
        }

        tts?.initTTS()
        if (viewModel?.sheetIdCount == 0) {
            viewModel?.sheetIdCount = 15
        }
        viewModel?.vpPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) { }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
            override fun onPageSelected(position: Int) {
//                Toast.makeText(mContext, "pos = " + position + " / viewModelSheetIdCount = " + viewModel?.sheetIdCount, Toast.LENGTH_SHORT).show()
                if (position >= 0 && position < viewModel?.sheetIdCount!!) {
                    switchFocusSheetInTab(viewModel?.sheets?.get(position)?.getTextView() as View)
                    //(viewModel?.adapterViewPager as MainViewModel.MyPagerAdapter).adapterSheetFragmentArray[position].textSize = viewModel!!.sheets[position].getTextSize()
                }
                viewModel?.currentTabPosition = position
            }
        })
        initializeHotKey()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.d(TAG, "onCreateOptionsMenu")
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected")
        when(item.itemId) {
            R.id.menuSaveOptionBtn-> save();
            R.id.menuEditSheetNameBtn-> makeDialogAndSetNewSheetName()
            R.id.menuDeleteSheetBtn->{
//                for (i in 1..sheets.size) {
//                    if (currentSheetId == sheets[i-1].getId()) {
//                        sheets.removeAt(i-1)
//                        break
//                    }
//                }
            }
            R.id.menuTextSizeIncreaseBtn-> viewModel?.contentTextSizeIncrease()
            R.id.menuTextSizeDecreaseBtn-> viewModel?.contentTextSizeDecrease()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        save()
    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.close()
    }

    private fun makeDialogAndSetNewSheetName() {
        // make Dialog
        val dlg: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        val ad: AlertDialog = dlg.create()
        ad.setTitle("Edit Name") //제목
        val inflater: LayoutInflater = LayoutInflater.from(applicationContext)
        val view = inflater.inflate(R.layout.dialog, root_layout, false)
        ad.setView(view) // 메시지
        // set New Sheet Name if the confirm button is clicked.
        view.dialogConfirmBtn.setOnClickListener {
            for (i in 1..viewModel?.sheets!!.size) {
                if (viewModel?.currentTabTextView?.id == viewModel?.sheets?.get(i - 1)?.getId()) {
                    viewModel?.sheets?.get(i - 1)?.getTextView()?.text = view.dialogEditBox.text
                    viewModel?.sheets?.get(i - 1)?.setName(view.dialogEditBox.text.toString())
                    break
                }
            }
            ad.dismiss()
        }
        ad.show()
    }

    fun onPlusIconClick(view: View) {
        viewModel?.addNewSheet(applicationContext, viewModel?.vpPager!!, ::switchFocusSheetInTab, ::addShowingSheetInTab)
    }

    /** Switch focus sheet in bottom tab
     * - change currentSheetId, currentTabTextView
     */
    private fun switchFocusSheetInTab(it:View) {
        Log.d(TAG, "switchFocusSheetInTab")

        val view = it as TextView
        if (view.id == viewModel?.currentSheetId) {
            return
        }
        val existTextView = viewModel?.currentTabTextView
        viewModel?.currentSheetId = view.id
        viewModel?.currentTabTextView = view
        existTextView?.setBackgroundColor(resources.getColor(R.color.colorDeactivatedSheet))
        view.setBackgroundColor(resources.getColor(R.color.colorActivatedSheet))
        Log.d(TAG,"id = " + viewModel?.currentSheetId + " / sheet id count = " + viewModel?.sheetIdCount!! )
//        Toast.makeText(this, "id = " + viewModel?.currentSheetId + " / sheet id count = " + viewModel?.sheetIdCount!!, Toast.LENGTH_SHORT).show()
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

    /** Add new title view of a sheet into the tab bottom side of screen
     * - If there is previous parent (linear layout) of view, it should be called removeView method.
     */
    private fun addShowingSheetInTab(view: View) {
        if (view.parent != null) {
            ((view.parent) as ViewGroup).removeView(view)
        }
        viewModel?.sheetSelectionTab?.addView(view)
    }

    /** Save data (the contents of sheets and so on)
     * 1. Move all data from adapterSheetFragmentArray to Sheets
     * 2. Set all data of sheets to PreferenceManager
     */
    private fun save() {
        Log.d(TAG, "save")

        // 현재 프레그 먼트 덩어리에 있는 것을 저장하여 올림
        for (i in 1..viewModel?.sheets!!.size) {
            if (viewModel?.adapterSheetFragmentArray?.size!! >= i) {
                val text: String = viewModel?.adapterSheetFragmentArray!![i-1].editText?.text.toString() // pager의 프레그먼트의 내용물(editText)에 접근
                val textSize: Float = viewModel?.adapterSheetFragmentArray!![i-1].textSize!! // pager의 프레그먼트의 내용물(textSize)에 접근
                viewModel?.sheets?.get(i-1)?.setContent(text)
                viewModel?.sheets?.get(i-1)?.setTextSize(textSize)
            }
        }

        for (i in 1..viewModel?.sheets!!.size) {
            val sheetNameKey = "sheetName$i"
            val sheetContentKey = "sheetContent$i"
            val sheetIdKey = "sheetId$i"
            val sheetTextSizeKey = "sheetTextSize$i"

            PreferenceManager.setString(this, sheetNameKey, viewModel?.sheets?.get(i-1)?.getName())
            PreferenceManager.setString(this, sheetContentKey, viewModel?.sheets?.get(i-1)?.getContent())
            PreferenceManager.setString(this, sheetIdKey, viewModel?.sheets?.get(i-1)?.getId().toString())
            PreferenceManager.setFloat(this, sheetTextSizeKey, viewModel?.sheets?.get(i-1)?.getTextSize()!!)
        }
        PreferenceManager.setInt(this, "sheetCount", viewModel?.sheets!!.size)
        PreferenceManager.setInt(this, "sheetIdCount", viewModel?.sheetIdCount!!)
        Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show()
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
        viewModel?.sheetSelectionTab = findViewById(R.id.tabInner)
        viewModel?.isFisrtStart = false
        viewModel?.sheetCount = PreferenceManager.getInt(this, "sheetCount")
        viewModel?.sheetIdCount = PreferenceManager.getInt(this, "sheetIdCount")
        if (viewModel?.sheetCount!! > 0) {
            for (i in 1..viewModel?.sheetCount!!) {
                val sheetNameKey = "sheetName$i"
                val sheetContentKey = "sheetContent$i"
                val sheetIdKey = "sheetId$i"
                val sheetTextSizeKey = "sheetTextSize$i"
                var sheetName = PreferenceManager.getString(this, sheetNameKey)
                var sheetContent = PreferenceManager.getString(this, sheetContentKey)
                var sheetId:String? = PreferenceManager.getString(this, sheetIdKey)
                var sheetTextSize:Float? = PreferenceManager.getFloat(this, sheetTextSizeKey)
                if (sheetTextSize == -1.0f) {
                    sheetTextSize = 10.0f
                }
                if (sheetId != null) {
                    val textView = TabTextView(applicationContext);
                    viewModel?.sheets?.add(Sheet(sheetId!!.toInt(), sheetName, sheetContent, textView, sheetTextSize))
                    val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    textView.layoutParams = params
                    textView.text = sheetName
                    textView.id = sheetId!!.toInt()
                    textView.setBackgroundColor(resources.getColor(R.color.colorDeactivatedSheet))
                    viewModel?.sheetOrder?.set(textView, i - 1)
                    textView.setOnClickListener {
                        switchFocusSheetInTab(it)
                        viewModel?.sheetOrder?.get(it as View)?.let { it1 ->
//                                Toast.makeText(this, "it1 = " + it1, Toast.LENGTH_SHORT).show()
                            viewModel?.vpPager?.setCurrentItem(it1, true)
                            viewModel?.currentTabPosition = viewModel?.sheetOrder?.get(it as View)!!
                        }
                    }
                    addShowingSheetInTab(textView)
                }
            }
            viewModel?.currentTabTextView = viewModel?.sheets?.get(0)?.getTextView()
            if (viewModel?.currentTabTextView != null) {
                viewModel?.currentSheetId = viewModel?.currentTabTextView!!.id
                viewModel?.currentTabTextView!!.setBackgroundColor(resources.getColor(R.color.colorActivatedSheet))
            }
            viewModel?.initViewPager(viewModel?.vpPager!!, supportFragmentManager)

        } else {
            Toast.makeText(this, "저장된 데이터가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

}
