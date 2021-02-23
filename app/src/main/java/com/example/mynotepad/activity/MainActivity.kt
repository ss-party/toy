package com.example.mynotepad.activity

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.mynotepad.R
import com.example.mynotepad.utility.PreferenceManager
import com.example.mynotepad.utility.TTSpeech
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
        tts = TTSpeech(this)

        clearData()

        if (viewModel?.isFirstStart == true) {
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
                    switchFocusSheetInTab(position)
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
            R.id.menuSaveOptionBtn-> saveAllIntoDB();
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
        saveAllIntoDB()
    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.close()
    }

    private fun switchFocusSheetInTab(position: Int) {
        viewModel?.switchFocusSheetInTab(position)
        viewModel?.updateFragmentToSheets()
        showAllData()
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
                if (viewModel?.currentTabTitleView?.id == viewModel?.sheets?.get(i - 1)?.getId()) {
                    viewModel?.sheets?.get(i - 1)?.getTabTitleView()?.text = view.dialogEditBox.text
                    viewModel?.sheets?.get(i - 1)?.setName(view.dialogEditBox.text.toString())
                    break
                }
            }
            ad.dismiss()
        }
        ad.show()
    }

    fun onPlusIconClick(view: View) {
        viewModel?.addNewSheet(viewModel?.vpPager!!)
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
        viewModel?.saveAllIntoDB()
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
        val result = viewModel?.initialize(this, supportFragmentManager)
        if (result == false) {
            Toast.makeText(this, "데이터 초기화 실패", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAllData() {
        for (i in 1..viewModel?.sheets!!.size) {
            val text: String = viewModel?.sheets!![i-1].getContent().toString()
            val title: String = viewModel!!.sheets[i-1]!!.getName()!!
            val viewId: Int = viewModel!!.sheets[i-1]!!.getTabTitleView()!!.id
            val sheetId: Int = viewModel!!.sheets[i-1]!!.getId()!!
            var length = text.length-1
            if (text.length >= 5) {
                length = 5
            }
            Log.d(TAG, "num = $i, content = ${text.substring(0, length)}, title = $title, viewId = $viewId sheetId = $sheetId")
        }
    }
}
