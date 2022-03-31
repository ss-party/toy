package com.example.sstoy.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sstoy.activity.MainViewModel
import com.example.sstoy.R


class SheetFragment : Fragment() {
    // Store instance variables
    private val TAG = "SheetFragment/kongyi123"
    var content: String? = null
    var textSize: Float? = null
    var editText:EditText? = null
    var idxOfSheetsArray: Int? = null
    private var viewModel: MainViewModel? = null

    // Inflate the view for the fragment based on layout XML
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d(TAG, "onCreateView")
        val view: View = inflater.inflate(R.layout.sheet_fragment, container, false)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        editText = view.findViewById(R.id.editText)
        viewModel?.softKeyboard?.addEditText(editText)
        editText?.setText("$content")
        if (textSize != null) editText?.textSize = textSize!!
        return view
    }

    fun initialize(content:String, textSize:Float, idxOfSheets:Int) {
        Log.d(TAG, "initialize")
        this.content = content
        this.textSize = textSize
        this.idxOfSheetsArray = idxOfSheets
    }
}