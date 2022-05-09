package com.example.model

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.common.MyNotification
import com.example.model.data.History
import com.example.model.data.Notice
import com.example.model.data.Schedule
import com.google.firebase.database.*
import kotlinx.coroutines.*
import java.lang.NullPointerException
import java.util.HashMap


object DataManager {
    val dataList: MutableLiveData<ArrayList<Schedule>> = MutableLiveData()
    var notice: MutableLiveData<String> = MutableLiveData()
    var hcnt: MutableLiveData<Long> = MutableLiveData()
    val hList: MutableLiveData<ArrayList<History>> = MutableLiveData()
    private var lineNumber:String = ""

    fun getLineNumber(context:Context, tt:Activity):String {
        var result = "none"
        if (context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.READ_PHONE_STATE) }
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(tt, arrayOf(Manifest.permission.READ_PHONE_STATE),1004)

        } else {
            try {
                result =
                    (tt.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).line1Number.toString()
            } catch (e:NullPointerException) {
                e.printStackTrace()
            }
        }
        lineNumber = result
        return result

    }

    fun getNotice() {
        Log.i("kongyi1220", "getNotice()")
        val query:Query = FirebaseDatabase.getInstance().reference.child("notice")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("kongyi1234", "changed")
                if (snapshot.exists()) {
                    snapshot.getValue(Notice::class.java)?.let {
                        Log.i("kongyi1234", "snapshot is exist : ${notice.value}")
                        notice.value = it.content.toString()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    fun getNewNumberForHistory() {
        Log.i("kongyi1220", "getNewNumberForHistory")

        val query:Query = FirebaseDatabase.getInstance().reference.child("history_cnt")
        Log.i("kongyi1220", "ref = ${query.ref}")
        var cnt:Long = 0
        var str:String? = null
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val get = snapshot.value

                Log.i("kongyi1220", "content = [${get}]")
                str = get.toString()
                Log.i("kyi1220", "str = [${str}]")
                hcnt.postValue(str!!.toLong())
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    fun getAllHistoryData(context:Context) {
        Log.i("kongyi1220", "getAllHistoryData")

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
                hList.postValue(historyList)
                val notificationEnable = getNotificationState(context)
                if (notificationEnable) {
                    val content = decideNotifyText(historyList)
                    val subjectLineNumber = getOnlySubjectLineNumber(historyList)
                    Log.i("kongyi1220", "lineNumber = ${lineNumber} / subjectLineNumber = ${subjectLineNumber}")
                    if (lineNumber != subjectLineNumber) {
                        MyNotification.doNotify(context, content) // 이거 대신 broadcast 하도록 해야한다.
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    private fun getOnlySubjectLineNumber(historyList: ArrayList<History>): String {
        var number:String? = "none"
        if (historyList.size == 0) return "none"
        historyList?.get(historyList.lastIndex)?.let {
            val latestAction = it

            when (latestAction.arg2) {
                "access" -> number = latestAction.arg4
                "pcal-schedule-new" -> number = latestAction.arg4
                "pcal-schedule-remove" -> number = latestAction.arg4
                "pcal-schedule-modify" -> number = latestAction.arg5
                "cal-schedule-new" -> number = latestAction.arg4
                "cal-schedule-remove" -> number = latestAction.arg4
                "cal-schedule-modify" -> number = latestAction.arg5
            }
        }
        return number!!
    }

    private fun decideNotifyText(historyList: ArrayList<History>): String {
        var content = "none"
        if (historyList.size == 0) {
            return "none"
        }
        historyList?.get(historyList.lastIndex)?.let {
            val latestAction = it

            when (latestAction.arg2) {
                "access" -> {
                    content = "휴대전화번호 [${latestAction.arg4}]가 [${latestAction.arg3}]에 접근하였습니다."
                }
                "pcal-schedule-new" -> {
                    content = "휴대전화번호 [${latestAction.arg4}]가 [${latestAction.arg2}] 동작을 했습니다.\n" +
                            "상세 내용 : { ${latestAction.arg3} }"
                }
                "pcal-schedule-remove" -> {
                    content = "휴대전화번호 [${latestAction.arg4}]가 [${latestAction.arg2}] 동작을 했습니다. \n" +
                            "상세 내용 : { ${latestAction.arg3} }"
                }
                "pcal-schedule-modify" -> {
                    content = "휴대전화번호 ${latestAction.arg5}가 [${latestAction.arg2}] 동작을 했습니다. \n" +
                            "수정 전 내용 : { ${latestAction.arg3} } \n" +
                            "수정 후 내용 : { ${latestAction.arg4} }"
                }
                "cal-schedule-new" -> {
                    content = "휴대전화번호 [${latestAction.arg4}]가 [${latestAction.arg2}] 동작을 했습니다.\n" +
                            "상세 내용 : { ${latestAction.arg3} }"
                }
                "cal-schedule-remove" -> {
                    content = "휴대전화번호 [${latestAction.arg4}]가 [${latestAction.arg2}] 동작을 했습니다. \n" +
                            "상세 내용 : { ${latestAction.arg3} }"
                }
                "cal-schedule-modify" -> {
                    content = "휴대전화번호 ${latestAction.arg5}가 [${latestAction.arg2}] 동작을 했습니다. \n" +
                            "수정 전 내용 : { ${latestAction.arg3} } \n" +
                            "수정 후 내용 : { ${latestAction.arg4} }"
                }
            }
        }

        return content
    }

    fun getAllScheduleData(id_list:String) {
        val scheduleList = ArrayList<Schedule>()
        val sortByAge:Query = FirebaseDatabase.getInstance().reference.child(id_list)
        sortByAge.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("kongyi1220", "onChanged")
                CoroutineScope(Dispatchers.Default).launch {
                    updateDataList(scheduleList, snapshot)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    // debounce logic.
    private var lastJob: Job? = null

    private suspend fun updateDataList(
        scheduleList: ArrayList<Schedule>,
        snapshot: DataSnapshot
    ) {
        if (lastJob != null) {
            lastJob!!.cancel()
        }
        lastJob = CoroutineScope(Dispatchers.Default).launch {
            delay(500)
            doUpdate(scheduleList, snapshot)
            lastJob = null
        }
    }

    private fun doUpdate(
        scheduleList: ArrayList<Schedule>,
        snapshot: DataSnapshot
    ) {
        scheduleList.clear()
        Log.i("kongyi0504", "scheduleList right after clear= {$scheduleList}")

        for (postSnapshot in snapshot.children) {
            if (!postSnapshot.exists()) {
                continue
            }
            for (postPostSnapshot in postSnapshot.children) {
                val get = postPostSnapshot.getValue(FirebasePost::class.java)
                get?.id?.let {
                    scheduleList.add(Schedule(get.id, get.date, get.title, get.content, get.color))
                }
            }
        }
        Log.i("kongyi0504", "scheduleList after adding = {$scheduleList}")
        dataList.postValue(scheduleList) // similar with postValue
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

    fun putSingleHistory(context: Context, command:String, arg1:String = "", arg2:String = "", arg3:String = "", arg4:String = "", arg5:String = "") {
        Log.i("kongyi1220TT", "command = ${command}, arg1 = $arg1, arg2 = $arg2, arg3 = $arg3, arg4 = $arg4, arg5 = $arg5")
        // arg1 : type
        val newId = "${1 + hcnt.value!!}"
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

    fun getNotificationState(context:Context):Boolean {
        val dataManager = PreferenceDataManager(context)
        if (dataManager.getInt("notification") == 1) {
            return true
        }
        return false
    }

    fun setNotificationState(context:Context, state:Boolean) {
        val dataManager = PreferenceDataManager(context)
        if (state) {
            dataManager.setInt("notification", 1)
        } else {
            dataManager.setInt("notification", 0)
        }
    }


    fun getUpdateState(context:Context):Boolean {
        val dataManager = PreferenceDataManager(context)
        if (dataManager.getInt("updateEnable") == 1) {
            return true
        }
        return false
    }

    fun setUpdateState(context:Context, state:Boolean) {
        val dataManager = PreferenceDataManager(context)
        if (state) {
            dataManager.setInt("updateEnable", 1)
        } else {
            dataManager.setInt("updateEnable", 0)
        }
    }
}