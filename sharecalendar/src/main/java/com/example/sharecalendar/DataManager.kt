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
    fun getScheduleDataForPeriod() {
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
                    val get = postSnapshot.getValue(FirebasePost::class.java)
                    Log.i("kongyi1220", "title = ${get?.title}, content = ${get?.content}, id = ${get?.id}")
                    //arrayData.add(get.toString())
                    get?.id?.let {
                        scheduleList.add(Schedule(get.id, get.date, get.title, get.content))
                    }
                    arrayIndex.add(key!!)
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

    fun removeSingleSchedule(date:String) {
        val ref = FirebaseDatabase.getInstance().reference
        //ref.child("/id_list/$date").setValue(null)
        ref.child("/id_list/$date").removeValue()
    }

    fun putSingleSchedule(date:String, title:String, content:String) {
        postFirebaseDatabase(true, date, date, title, content)
    }

    fun postFirebaseDatabase(add: Boolean, id:String, date:String, title:String, content:String) {
        val mPostReference = FirebaseDatabase.getInstance().reference
        val childUpdates: MutableMap<String, Any?> = HashMap()
        var postValues: Map<String?, Any?>? = null

        if (add) {
            val post = FirebasePost(date, title, content, date)
            postValues = post.toMap()
        }
        childUpdates["/id_list/$id"] = postValues
        mPostReference.updateChildren(childUpdates)
    }
}