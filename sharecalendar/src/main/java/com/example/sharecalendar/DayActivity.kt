package com.example.sharecalendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class DayActivity : AppCompatActivity() {
    private lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day)
        button = findViewById(R.id.closeBtn)
        button.setOnClickListener {
            onClickClose()
        }
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    fun onClickClose() {
        finish()
    }
}