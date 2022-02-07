package com.example.sharecalendar;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class FirebasePostForH {
    public String id;
    public String command;
    public String arg1;
    public String arg2;
    public String arg3;


    public FirebasePostForH() {

    }

    public FirebasePostForH(String id, String command, String arg1, String arg2, String arg3) {
        this.id = id;
        this.command = command;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
    }

    //val post = FirebasePost(puttingId, arg1, arg2, command, arg3)
    @Exclude
    public Map<String, Object> toMapForHistory() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("command", command);
        result.put("arg1", arg1);
        result.put("arg2", arg2);
        result.put("arg3", arg3);
        return result;
    }
}
