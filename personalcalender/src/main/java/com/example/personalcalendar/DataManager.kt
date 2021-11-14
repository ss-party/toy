package com.example.personalcalendar

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.personalcalendar.data.Notice
import com.example.personalcalendar.data.Schedule
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList


object DataManager {
    val dataList: MutableLiveData<ArrayList<Schedule>> = MutableLiveData()
    var notice: MutableLiveData<String> = MutableLiveData()

    fun getNotice() {
        val query:Query = FirebaseDatabase.getInstance().reference.child("notice")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("kongyi1234", "changed")
                if (snapshot.exists()) {
                    val get = snapshot.getValue(Notice::class.java)
                    notice.value = get?.content.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
    fun getAllScheduleData() {
        val scheduleList = ArrayList<Schedule>()
        val sortByAge:Query = FirebaseDatabase.getInstance().reference.child("pid_list")
        sortByAge.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("kongyi1220", "onchanged")
                scheduleList.clear()
                for(postSnapshot in snapshot.children) {
                    if (!postSnapshot.exists()) {
                        continue
                    }
                    for (postPostSnapshot in postSnapshot.children) {
                        Log.i("kongyi1220", "key = " + postPostSnapshot.key.toString())
                        val get = postPostSnapshot.getValue(FirebasePost::class.java)
                        Log.i(
                            "kongyi1220",
                            "title = ${get?.title}, content = ${get?.content}, id = ${get?.id}"
                        )
                        get?.id?.let {
                            scheduleList.add(Schedule(get.id, get.date, get.title, get.content, get.color))
                        }
                    }
                }
                dataList.value = scheduleList
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }



    fun getSingleScheduleById(String:Int): Schedule? {
        return null
    }

    fun removeSingleSchedule(date:String, id:String) { // color 넣어야 할지
        val ref = FirebaseDatabase.getInstance().reference
        //ref.child("/pid_list/$date").setValue(null)
        Log.i("kongyi12200", "date = " + date + " / id = " + id)
        ref.child("/pid_list/$date/$id").removeValue()
    }

    fun removeDayAllSchedule(date:String) { // color 넣어야 할지
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("/pid_list/$date").removeValue()
    }

    fun putSingleSchedule(date:String, title:String, content:String, color:String, id:String) {
        Log.i("kongyi1220A", "id = " + id + " " + Random().nextInt())
        if ("no_id" == id) {
            Log.i("kongyi1220A", "newId = here!!!")

            val newId = Utils.bytesToHex1(Utils.sha256(Random().nextInt().toString()))
            postFirebaseDatabase(true, newId, date, title, content, color)
        } else {
            Log.i("kongyi1220A", "newId = there!!!")
            postFirebaseDatabase(false, id, date, title, content, color)
        }
    }

    fun postFirebaseDatabase(add: Boolean, id:String, date:String, title:String, content:String, color:String) {
        val mPostReference = FirebaseDatabase.getInstance().reference
        val childUpdates: MutableMap<String, Any?> = HashMap()
        var postValues: Map<String?, Any?>? = null
        Log.i("kongyi111", "add = " + add + " / id = " + id);

        val post = FirebasePost(id, title, content, date, color)
        postValues = post.toMap()
        childUpdates["/pid_list/$date/$id"] = postValues
        mPostReference.updateChildren(childUpdates)
    }

    fun setNotice(content: String) {
        postFirebaseDatabase(content)
        notice.value = content
    }

    fun postFirebaseDatabase(content: String) {
        val mPostReference = FirebaseDatabase.getInstance().reference
        val childUpdates: MutableMap<String, Any?> = HashMap()
        val result = HashMap<String, Any>()
        result["content"] = content
        childUpdates["/notice/"] = result
        mPostReference.updateChildren(childUpdates)
    }
}