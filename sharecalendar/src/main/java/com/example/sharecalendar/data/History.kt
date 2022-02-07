package com.example.sharecalendar.data

import java.io.Serializable

class History(arg1: String?, arg2: String?, arg3: String?, arg4: String?, arg5: String?) : Serializable {
    val arg1 = arg1
    val arg2 = arg2
    val arg3 = arg3
    val arg4 = arg4
    val arg5 = arg5

    override fun toString(): String {
        return "id = $arg1, command = $arg2, arg1 = $arg3, arg2 = $arg4, arg3 = $arg5"
    }
}