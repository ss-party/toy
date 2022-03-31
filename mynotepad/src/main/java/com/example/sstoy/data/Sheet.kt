package com.example.sstoy.data

import android.widget.TextView
import com.example.sstoy.view.SheetFragment

class Sheet {
    private var name: String? = null
    private var content: String? = null
    private var id: Int? = null
    private var tabTitleView: TextView? = null
    private var textSize: Float? = null
    private var sheetFragment: SheetFragment? = null

    constructor(id: Int, name: String?, content: String?, textView: TextView?, textSize: Float?) {
        this.id = id
        this.name = name
        this.content = content
        this.tabTitleView = textView
        this.textSize = textSize
    }

    fun getSheetFragment(): SheetFragment? {
        return sheetFragment
    }

    fun setSheetFragment(sheetFragment: SheetFragment) {
        this.sheetFragment = sheetFragment
    }

    fun getId(): Int? {
        return id
    }

    fun getName(): String? {
        return name
    }

    fun getContent(): String? {
        return content
    }

    fun setContent(text: String?) {
        this.content = text
    }

    fun getTabTitleView(): TextView? {
        return this.tabTitleView
    }

    fun setName(text: String) {
        this.name = text
    }

    fun getTextSize(): Float? {
        return this.textSize
    }

    fun setTextSize(size:Float) {
        this.textSize = size
        updateTextSizeToEditText()
    }

    private fun updateTextSizeToEditText() {
        if (sheetFragment != null) {
            sheetFragment?.editText?.textSize = textSize!!
        }
    }
}