package com.example.common

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext




// reference
// https://doitddo.tistory.com/99

class MyCalendarView @JvmOverloads constructor(
    context: Context, attrs:AttributeSet? = null, defStyle: Int = 0
): FrameLayout(context) {

    private var mImg: ImageView
    private val IMAGEVIEW_TAG = "드래그 이미지"

    init {
        inflate(context, R.layout.my_calendar, this)
        mImg = findViewById(R.id.image)
        mImg.tag = IMAGEVIEW_TAG

        mImg.setOnLongClickListener {
            // 태그 생성
            Log.i("kongyi0424", "setOnLongClickListener")

//            val item: ClipData.Item = ClipData.Item(it.tag as CharSequence)
//            val mimeTypes = arrayOf<String>(ClipDescription.MIMETYPE_TEXT_PLAIN)
//            val data = ClipData(it.tag.toString(),mimeTypes, item)
//            val shadowBuilder = DragShadowBuilder(it)
//            it.startDrag(
//                data,  // data to be dragged
//                shadowBuilder,  // drag shadow
//                it,  // 드래그 드랍할  Vew
//                0 // 필요없은 플래그
//            )

            val shadowBuilder = DragShadowBuilder(it)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                it.startDragAndDrop(null, shadowBuilder, it, 0)
            }

            it.visibility = View.INVISIBLE
            true
        }

        findViewById<LinearLayout>(R.id.view1).setOnDragListener(DragListener())
        findViewById<LinearLayout>(R.id.view2).setOnDragListener(DragListener())
        findViewById<LinearLayout>(R.id.view3).setOnDragListener(DragListener())
    }

    inner class DragListener : OnDragListener {
//        var normalShape: Drawable = resources.getDrawable(R.color.teal_700)
//        var targetShape: Drawable = resources.getDrawable(R.color.purple_200)

        override fun onDrag(v: View, event: DragEvent): Boolean {
            // 이벤트 시작
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> { // 각 리스너의 드래그 앤 드롭 시작 상태 (3번 call됨)
                    Log.i("kongyi0424", "ACTION_DRAG_STARTED")
                }
                DragEvent.ACTION_DRAG_ENTERED -> { // 이미지가 들어옴
                    Log.i("kongyi0424", "ACTION_DRAG_ENTERED")
                    // 이미지가 들어왔다는 것을 알려주기 위해 배경이미지 변경
//                    v.background = targetShape
                }
                DragEvent.ACTION_DRAG_EXITED -> { // 이미지가 나감
                    Log.i("kongyi0424", "ACTION_DRAG_EXITED")
//                    v.background = normalShape
                }
                DragEvent.ACTION_DROP -> {
                    Log.i("kongyi0424", "ACTION_DROP")
                    if (v === findViewById<View>(R.id.view1) ||
                        v === findViewById<View>(R.id.view2) ||
                        v === findViewById<View>(R.id.view3)) {
                        val view = event.localState as View
                        (view.parent as ViewGroup).removeView(view)

                        val containView = v as LinearLayout
                        containView.addView(view)
//                        view.visibility = VISIBLE
                    }
                }
                DragEvent.ACTION_DRAG_ENDED -> { // 각 리스너의 드래그 앤 드롭 종료 상태 (3번 call됨)
                    Log.i("kongyi0424", "ACTION_DRAG_ENDED")

                    val view = event.localState as View
                    view.visibility = VISIBLE
//                    v.background = normalShape // go back to normal shape
                }
                else -> {
                }
            }
            return true
        }
    }
}
