package com.example.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class TimeChangeReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
//        val intentFilter = IntentFilter(Intent.ACTION_TIME_CHANGED)
            Log.i("kongyi000", "receive : ${p1?.action}")

        Log.i("kongyi000", "receive : ${p1?.action}")
        Log.i("kongyi000", "receive : ${p1?.action}")

//            if(Intent.ACTION_DATE_CHANGED == intent!!.action) {
//            }
    }
}