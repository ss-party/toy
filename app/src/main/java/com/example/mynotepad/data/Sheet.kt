package com.example.mynotepad.data

import android.widget.TextView

class Sheet {
    private var name: String? = null
    private var content: String? = null
    private var id: Int? = null
    private var textView: TextView? = null
    private var textSize: Float? = null

    constructor(id: Int, name: String?, content: String?, textView: TextView?, textSize: Float?) {
        this.id = id
        this.name = name
        this.content = content
        this.textView = textView
        this.textSize = textSize
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

    fun setText(text: String) {
        setContent(text)
    }

    fun setContent(text: String) {
        this.content = text
    }

    fun getTextView(): TextView? {
        return this.textView
    }

    fun setName(text: String) {
        this.name = text
    }

    fun getTextSize(): Float? {
        return this.textSize
    }

    fun setTextSize(size:Float) {
        this.textSize = size
    }
}