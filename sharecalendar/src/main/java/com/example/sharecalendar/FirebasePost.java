package com.example.sharecalendar;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class FirebasePost {
    public String id;
    public String title;
    public String content;
    public String date;
    public String color;

    public FirebasePost() {

    }

    public FirebasePost(String arg1, String arg2, String arg3, String arg4, String arg5) {
        this.id = arg1;
        this.title = arg2;
        this.content = arg3;
        this.date = arg4;
        this.color = arg5;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("title", title);
        result.put("content", content);
        result.put("date", date);
        result.put("color", color);
        return result;
    }
}

