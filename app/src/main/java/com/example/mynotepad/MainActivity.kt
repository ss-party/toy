package com.example.mynotepad

import android.app.AlertDialog
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mynotepad.PreferenceManager.setFloat
import com.example.mynotepad.PreferenceManager.setInt
import com.example.mynotepad.PreferenceManager.setString
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog.view.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var tts: TextToSpeech? = null
    private var sheetCount: Int = 0
    private var sheetSelectionTab: LinearLayout? = null
    private var sheets:ArrayList<Sheet> = ArrayList<Sheet>()
    private var currentSheetId:Int = 0
    private var currentTextView:TextView? = null
    private var currentTextSize:Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



//        Toast.makeText(this, "dpi = " + this.resources.displayMetrics.densityDpi, Toast.LENGTH_SHORT).show()

        val t:ThreadA = ThreadA()
        initTTS();
        var speed = 1.0f
        var pitch = 1.0f
        sheetCount = PreferenceManager.getInt(this, "sheetCount")
        currentTextSize = PreferenceManager.getFloat(this, "textSize")
        sheetSelectionTab = findViewById(R.id.tab)
        if (currentTextSize == 0f) {
            currentTextSize = 10.0f
        }

        if (sheetCount > 0) {
            for (i in 1..sheetCount) {
                val sheetNameKey = "sheetName$i"
                val sheetContentKey = "sheetContent$i"
                val sheetIdKey = "sheetId$i"
                var sheetName = PreferenceManager.getString(this, sheetNameKey)
                var sheetContent = PreferenceManager.getString(this, sheetContentKey)
                var sheetId:String? = PreferenceManager.getString(this, sheetIdKey)
                if (sheetId != null) {
                    val textView = TextView(applicationContext);
                    sheets.add(Sheet(sheetId!!.toInt(), sheetName, sheetContent, textView))
                    val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    textView.layoutParams = params
                    textView.text = sheetName
                    textView.id = sheetId!!.toInt()
                    textView.background = getDrawable(R.drawable.edge)
                    textView.setOnClickListener {
                        val view = it as TextView
                        editText.setText(getContentById(view.id))
                        editText.textSize = currentTextSize
                        currentTextView?.background = getDrawable(R.drawable.edge)
                        currentSheetId = view.id
                        currentTextView = view
                        view.setBackgroundColor(resources.getColor(R.color.colorAccent))
                    }
                    addShowingSheet(textView)
                }
            }
        } else {
            Toast.makeText(this, "저장된 데이터가 없습니다.", Toast.LENGTH_SHORT).show()
//            } else {
//                editText.setText(text)
//            }
        }

        val editText: EditText? = findViewById<EditText>(R.id.editText)
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
                        Toast.makeText(v?.context, "saved", Toast.LENGTH_SHORT).show()
                        save();
                    }
                    KeyEvent.KEYCODE_PLUS -> {
                        Toast.makeText(v?.context, "plus pressed", Toast.LENGTH_SHORT).show()
                        editText.textSize = ++currentTextSize
                    }
                    KeyEvent.KEYCODE_MINUS -> {
                        Toast.makeText(v?.context, "minus pressed", Toast.LENGTH_SHORT).show()
                        editText.textSize = --currentTextSize
                    }

                    KeyEvent.KEYCODE_F5 -> {
                        var string = editText.text.toString()
                        t.input(string, tts)
                        t.start()
                    }
                    KeyEvent.KEYCODE_PAGE_UP -> if (event?.isCtrlPressed == true) {
                        if (speed < 3) {
                            speed += 0.25f;
                            Toast.makeText(v?.context, "speed = " + speed, Toast.LENGTH_SHORT).show()
                            tts?.setPitch(pitch)
                            tts?.setSpeechRate(speed)
                        }
                    }
                    KeyEvent.KEYCODE_PAGE_DOWN -> if (event?.isCtrlPressed == true) {
                        if (speed > 1) {
                            speed -= 0.25f;
                            Toast.makeText(v?.context, "speed = " + speed, Toast.LENGTH_SHORT).show()
                            tts?.setPitch(pitch)
                            tts?.setSpeechRate(speed)
                        }
                    }
                }
                true
            }
            false
        }
    }

    private fun findInput() {

    }

    private fun findNext() {

    }

    private fun initTTS() {
        tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.KOREAN)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "TTS Init failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getContentById(id: Int):String {
        for (i in 1..sheets.size) {
            if (sheets[i-1].getId() == id) {
                return sheets[i-1].getContent()!!
                break
            }
        }
        return "error"
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menuSaveOptionBtn->{
                save();
            }
            R.id.menuEditSheetNameBtn->{
                val dlg: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                val ad:AlertDialog = dlg.create()
                ad.setTitle("Edit Name") //제목
                val inflater: LayoutInflater = LayoutInflater.from(applicationContext)
                val view = inflater.inflate(R.layout.dialog, root_layout, false)
                ad.setView(view) // 메시지
                view.dialogConfirmBtn.setOnClickListener {
                    for (i in 1..sheets.size) {
                        if (currentTextView?.id == sheets[i-1].getId()) {
                            sheets[i-1].getTextView()?.text = view.dialogEditBox.text
                            sheets[i-1].setName(view.dialogEditBox.text.toString())
                            break
                        }
                    }
                    ad.dismiss()
                }
                ad.show()
            }
            R.id.menuDeleteSheetBtn->{
//                for (i in 1..sheets.size) {
//                    if (currentSheetId == sheets[i-1].getId()) {
//                        sheets.removeAt(i-1)
//                        break
//                    }
//                }
            }
            R.id.menuTextSizeIncreaseBtn-> editText.textSize = ++currentTextSize
            R.id.menuTextSizeDecreaseBtn-> editText.textSize = --currentTextSize
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
            tts = null
        }
    }

   class ThreadA : Thread() {
       private var string:String = ""

       var prev = 0
       var cur = 0
       var speakContent:String = ""
       var tts: TextToSpeech? = null
       override fun run() {
           for (i in 0..string.length) {
               if (string[i] == '.') {
                   cur = i
                   speakContent = string.substring(prev, cur)
                   prev = cur+1
                   ttsSpeak(speakContent)
                   while (tts?.isSpeaking == true) {
                       sleep(1000)
                   }
               }
           }

       }

       fun input(string:String, tts:TextToSpeech?) {
           this.string = string
           this.tts = tts
       }

       private fun ttsSpeak(str: String) {
           tts?.speak(str, TextToSpeech.QUEUE_ADD, null, null)
       }
   }

    fun onPlusIconClick(view: View) {
        val textView = TextView(applicationContext);
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        textView.layoutParams = params
        textView.text = "newSheet"
        textView.id = ++sheetCount
        textView.background = getDrawable(R.drawable.edge)
        textView.setOnClickListener {
            val view = it as TextView
            editText.textSize = currentTextSize
            editText.setText(getContentById(view.id))
            currentTextView?.background = getDrawable(R.drawable.edge)

            currentSheetId = view.id
            currentTextView = view
            view.setBackgroundColor(resources.getColor(R.color.colorAccent))
        }
        sheets.add(Sheet(sheetCount, "newSheet", "", textView))
        addShowingSheet(textView)
    }

    private fun addShowingSheet(view: View) {
        sheetSelectionTab?.addView(view)
    }

    private fun save() {
        for (i in 1..sheets.size) {
            if (sheets[i-1].getId() == currentTextView?.id) {
                sheets[i-1].setText(editText.text.toString())
                break
            }
        }

        for (i in 1..sheets.size) {
            val sheetNameKey = "sheetName$i"
            val sheetContentKey = "sheetContent$i"
            val sheetId = "sheetId$i"

            setString(this, sheetNameKey, sheets[i-1].getName())
            setString(this, sheetContentKey, sheets[i-1].getContent())
            setString(this, sheetId, sheets[i-1].getId().toString())
        }
        setInt(this, "sheetCount", sheets.size)
        setFloat(this, "textSize", currentTextSize)
    }
}
