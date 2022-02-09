package com.example.sharecalendar

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.sharecalendar.data.History
import com.example.sharecalendar.data.Notice
import com.example.sharecalendar.data.Schedule
import com.google.firebase.database.*
import java.util.HashMap


object DataManager {
    val dataList: MutableLiveData<ArrayList<Schedule>> = MutableLiveData()
    var notice: MutableLiveData<String> = MutableLiveData()
    var hcnt: MutableLiveData<Long> = MutableLiveData()
    val hList: MutableLiveData<ArrayList<History>> = MutableLiveData()
    var newCount = 0

    const val TYPE_HISTORY:String = "HISTORY"
    const val TYPE_SCHEDULE:String = "SCHEDULE"


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

    fun getNewNumberForHistory() {
        val query:Query = FirebaseDatabase.getInstance().reference.child("history_cnt")
        Log.i("kongyi1220", "ref = ${query.ref}")
        var cnt:Long = 0
        var str:String? = null
        query.get().addOnCompleteListener {
            if (it.isSuccessful) {
                Log.i("kyi1220", "content = [${it.result?.value}]")
                str = it.result?.value.toString()
                Log.i("kyi1220", "str = [${str}]")
                hcnt.value = str!!.toLong()
            } else {
                Log.i("kyi1220", "fail")
            }
        }
    }

    fun getAllHistoryData() {
        val historyList = ArrayList<History>()
        val sortByAge:Query = FirebaseDatabase.getInstance().reference.child("history")
        sortByAge.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("kongyi1220", "onchanged")
                historyList.clear()
                for(postSnapshot in snapshot.children) {
                    if (!postSnapshot.exists()) {
                        continue
                    }
                    Log.i("kongyi1220", "key = " + postSnapshot.key.toString())
                    val get = postSnapshot.getValue(FirebasePostForH::class.java)
                    Log.i(
                        "kongyi1220",
                        "title = ${get?.arg2}, content = ${get?.arg3}, id = ${get?.arg1}"
                    )
                    get?.arg1?.let {
                        historyList.add(History(get.id, get.command, get.arg1, get.arg2, get.arg3))
                    }
                }
                hList.value = historyList
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    fun getAllScheduleData(id_list:String) {
        val scheduleList = ArrayList<Schedule>()
        val sortByAge:Query = FirebaseDatabase.getInstance().reference.child(id_list)
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

    fun getScheduleDataInDate(date:String): ArrayList<Schedule> {
        val list = ArrayList<Schedule>()
        if (dataList.value != null) {
            for (node in dataList.value!!) {
                if (node.date == date) {
                    list.add(node)
                }
            }
        }
        return list
    }

    fun getSingleScheduleById(String:Int): Schedule? {
        return null
    }

    fun removeSingleSchedule(id_list:String, date:String, id:String) { // color 넣어야 할지
        val ref = FirebaseDatabase.getInstance().reference
        //ref.child("/id_list/$date").setValue(null)
        Log.i("kongyi12200", "date = " + date + " / id = " + id)
        ref.child("/$id_list/$date/$id").removeValue()
    }

    fun removeDayAllSchedule(id_list:String, date:String) { // color 넣어야 할지
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("/$id_list/$date").removeValue()
    }

    fun putSingleSchedule(id_list:String, date:String, title:String, content:String, color:String, id:String) {
        Log.i("kongyi1220A", "id = " + id)
        if (id == "no_id") {
            val newId = Utils.bytesToHex1(Utils.sha256(date+title+content))
            postFirebaseDatabaseForPutSchedule(id_list, true, newId, date, title, content, color)
        } else {
            postFirebaseDatabaseForPutSchedule(id_list,  false, id, date, title, content, color)
        }
    }


    fun putSingleHistory(command:String, arg1:String = "", arg2:String = "", arg3:String = "", arg4:String = "", arg5:String = "") {
        Log.i("kongyi1220A", "command = ${command}, arg1 = $arg1, arg2 = $arg2, arg3 = $arg3, arg4 = $arg4, arg5 = $arg5")
        // arg1 : type
        val newId = "${++newCount + hcnt.value!!}"
        //Utils.bytesToHex1(Utils.sha256(""+System.currentTimeMillis()))
        postFirebaseDatabaseForPutHistory(true, newId, command, arg1, arg2, arg3)
    }

    private fun postFirebaseDatabaseForPutSchedule(id_list:String, add: Boolean, id:String, date:String, title:String, content:String, color:String) {
        val mPostReference = FirebaseDatabase.getInstance().reference
        val childUpdates: MutableMap<String, Any?> = HashMap()
        var postValues: Map<String?, Any?>? = null
        Log.i("kongyi111", "add = $add / id = $id");
        var puttingId = Utils.bytesToHex1(Utils.sha256(date+title+content))
        if (!add) {
            puttingId = id
        }

        val post = FirebasePost(puttingId, title, content, date, color)
        postValues = post.toMap()
        childUpdates["/$id_list/$date/$id"] = postValues
        mPostReference.updateChildren(childUpdates)
    }
/*
    히스토리 기능
    1. 히스토리 마지막 넘버. - get/put
    2. System.millisec로 time를 만들고, 히스토리 넘버는 별도임.
    3. id 주소에 데이터를 입력하되 주입값으로 히스토리 넘버 포함시킴.
    4. 동시에 히스토리 마지막 넘버 put으로 update.
*/
    private fun postFirebaseDatabaseForPutHistory(add: Boolean, id:String, command:String, arg1:String, arg2:String, arg3:String) {
        val mPostReference = FirebaseDatabase.getInstance().reference
        val childUpdates: MutableMap<String, Any?> = HashMap()
        var postValues: Map<String?, Any?>? = null
        Log.i("kongyi111", "add = $add / id = $id");

        val post = FirebasePostForH(id, command, arg1, arg2, arg3)
        postValues = post.toMapForHistory()
        childUpdates["/history/${id}"] = postValues
        mPostReference.updateChildren(childUpdates)

        Log.i("kongyi222", "add = $add / id = $id");
        val childUpdates2: MutableMap<String, Any?> = HashMap()
        if (add) {
            Log.i("kongyi333", "add = $add / id = $id / hcnt = ${hcnt.value}");

//            postValues = post.toMapForHistoryCnt(hcnt.value!!+1)
            childUpdates2["/history_cnt"] = (id).toString()
            mPostReference.updateChildren(childUpdates2)
        }
    }


    fun setNotice(content: String) {
        postFirebaseDatabaseForPutSchedule(content)
        notice.value = content
    }

    fun postFirebaseDatabaseForPutSchedule(content: String) {
        val mPostReference = FirebaseDatabase.getInstance().reference
        val childUpdates: MutableMap<String, Any?> = HashMap()
        val result = HashMap<String, Any>()
        result["content"] = content
        childUpdates["/notice/"] = result
        mPostReference.updateChildren(childUpdates)
    }
}