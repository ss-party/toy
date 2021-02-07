package com.example.mynotepad

import android.widget.TextView

class Sheet {
    private var name: String? = null
    private var content: String? = null
    private var id: Int? = null
    private var textView: TextView? = null

    constructor(id:Int, name: String?, content: String?, textView :TextView?) {
        this.id = id
        this.name = name
        this.content = content
        this.textView = textView
    }

    fun getId():Int? {
        return id
    }

    fun getName():String? {
        return name
    }

    fun getContent():String? {
        return content
    }

    fun setText(text:String) {
        setContent(text)
    }

    fun setContent(text:String) {
        this.content = text
    }

    fun getTextView():TextView? {
        return this.textView
    }

    fun setName(text:String) {
        this.name = text
    }

}