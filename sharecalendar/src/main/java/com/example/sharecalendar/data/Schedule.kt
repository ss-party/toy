package com.example.sharecalendar.data
import java.io.Serializable

data class Schedule (
    var id: String,
    var date: String,
    var title:String,
    var content:String
) : Serializable


