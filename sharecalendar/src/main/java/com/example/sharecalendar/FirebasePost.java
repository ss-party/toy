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

    public FirebasePost(String id, String title, String content, String date, String color) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.color = color;
    }

    public FirebasePost(String content) {
        this.content = content;
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
