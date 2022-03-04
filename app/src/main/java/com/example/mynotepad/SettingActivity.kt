package com.example.mynotepad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import com.example.model.DataManager

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        if (DataManager.getNotificationState(this)) {
            findViewById<CheckBox>(R.id.notificationCheckBox).isChecked = true
        }

        findViewById<Button>(R.id.okButton).setOnClickListener {
            val checkState = findViewById<CheckBox>(R.id.notificationCheckBox).isChecked
            DataManager.setNotificationState(this, checkState)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val checkState = findViewById<CheckBox>(R.id.notificationCheckBox).isChecked
        DataManager.setNotificationState(this, checkState)
    }
}