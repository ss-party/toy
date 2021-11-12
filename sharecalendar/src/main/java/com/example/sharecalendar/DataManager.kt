package com.example.sharecalendar

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.sharecalendar.data.Schedule
import com.google.firebase.database.*
import java.util.HashMap


object DataManager {
    val arrayData = ArrayList<String>()
    val arrayIndex = ArrayList<String>()
    val dataList: MutableLiveData<ArrayList<Schedule>> = MutableLiveData()
    fun getAllScheduleData() {
        val scheduleList = ArrayList<Schedule>()
        val sortByAge:Query = FirebaseDatabase.getInstance().reference.child("id_list")
/*      The way to only get 1 time.
//        sortByAge.get().addOnSuccessListener {
//                arrayData.clear()
//                arrayIndex.clear()
//                for(postSnapshot in it.children) {
//                    val key = postSnapshot.key
//                    val get = postSnapshot.getValue(FirebasePost::class.java)
//                    Log.i("kongyi1220", "title = ${get?.title}, content = ${get?.content}, date = ${get?.date}")
//                    //arrayData.add(get.toString())
//                    get?.id?.let {
//                        scheduleList.add(Schedule(get.id, get.date, get.title, get.content))
//                    }
//                    arrayIndex.add(key!!)
//                }
//                dataList.value = scheduleList
//        }.addOnFailureListener {
//            Log.e("kongyi1220", "Error getting data", it)
//        }
*/
        sortByAge.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("kongyi1220", "onchanged")
                arrayData.clear()
                arrayIndex.clear()
                scheduleList.clear()
                for(postSnapshot in snapshot.children) {
                    if (!postSnapshot.exists()) {
                        continue
                    }
                    val key = postSnapshot.key
                    for (postPostSnapshot in postSnapshot.children) {
                        Log.i("kongyi1220", "key = " + postPostSnapshot.key.toString())
                        val get = postPostSnapshot.getValue(FirebasePost::class.java)
                        Log.i(
                            "kongyi1220",
                            "title = ${get?.title}, content = ${get?.content}, id = ${get?.id}"
                        )
                        //arrayData.add(get.toString())
                        get?.id?.let {
                            scheduleList.add(Schedule(get.id, get.date, get.title, get.content, get.color))
                        }
                        arrayIndex.add(key!!)
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
        //ref.child("/id_list/$date").setValue(null)
        Log.i("kongyi12200", "date = " + date + " / id = " + id)
        ref.child("/id_list/$date/$id").removeValue()
    }

    fun removeDayAllSchedule(date:String) { // color 넣어야 할지
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("/id_list/$date").removeValue()
    }

    fun putSingleSchedule(date:String, title:String, content:String, color:String, id:String) {
        Log.i("kongyi1220A", "id = " + id)
        if (id == "no_id") {
            val newId = Utils.bytesToHex1(Utils.sha256(date+title+content))
            postFirebaseDatabase(true, newId, date, title, content, color)
        } else {
            postFirebaseDatabase(false, id, date, title, content, color)
        }
    }

    fun postFirebaseDatabase(add: Boolean, id:String, date:String, title:String, content:String, color:String) {
        val mPostReference = FirebaseDatabase.getInstance().reference
        val childUpdates: MutableMap<String, Any?> = HashMap()
        var postValues: Map<String?, Any?>? = null
        Log.i("kongyi111", "add = " + add + " / id = " + id);
        var puttingId = Utils.bytesToHex1(Utils.sha256(date+title+content))
        if (!add) {
            puttingId = id
        }

        val post = FirebasePost(puttingId, title, content, date, color)
        postValues = post.toMap()
        childUpdates["/id_list/$date/$id"] = postValues
        mPostReference.updateChildren(childUpdates)
    }
}