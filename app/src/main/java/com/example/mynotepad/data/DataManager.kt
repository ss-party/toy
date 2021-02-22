package com.example.mynotepad.data

import android.content.Context
import com.example.mynotepad.utility.PreferenceManager

class DataManager(private val context:Context) {
    /**
     * float 값 로드
     * @param key
     * @return
     */
    fun getFloat(key: String?): Float {
        return PreferenceManager.getFloat(context, key)
    }

    /**
     * int 값 로드
     * @param key
     * @return
     */
    fun getInt(key: String?): Int {
        return PreferenceManager.getInt(context, key)
    }

    /**
     * int 값 저장
     * @param key
     * @param value
     */
    fun setInt(key: String?, value: Int) {
        PreferenceManager.setInt(context, key, value)
    }

    /**
     * float 값 저장
     * @param key
     * @param value
     */
    fun setFloat(key: String?, value: Float) {
        PreferenceManager.setFloat(context, key, value)
    }

    /**
     * String 값 로드
     * @param key
     * @return
     */
    fun getString(key: String?): String? {
        return PreferenceManager.getString(context, key)
    }

    /**
     * String 값 저장
     * @param key
     * @param value
     */
    fun setString(key: String?, value: String?) {
        PreferenceManager.setString(context, key, value)
    }
}