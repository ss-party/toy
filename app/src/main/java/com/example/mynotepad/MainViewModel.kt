package com.example.mynotepad

import android.app.Application
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import java.util.ArrayList

class MainViewModel(application: Application) : AndroidViewModel(application) {
    var currentSheetId:Int = 0
    var currentTextView: TextView? = null
    var isFisrtStart = true
    var viewModel_LocalValue: String? = null
        private set
    var sheetCount: Int = 0
    var sheetIdCount:Int = 0
    var sheets: ArrayList<Sheet> = ArrayList<Sheet>()
    var sheetSelectionTab: LinearLayout? = null
    var currentTextSize:Float = 10.0f

    fun putViewModel_LocalValue(str: String?) {
        viewModel_LocalValue = str
    }
}