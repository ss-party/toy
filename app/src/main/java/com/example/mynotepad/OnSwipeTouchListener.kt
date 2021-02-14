package com.example.mynotepad

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

// 빠른 스크롤을 직접 구현하기 위해 필요한 리스너.
// but 직접 구현할 필요가 없었음.
// ScrollView에서 지원함.
open class OnSwipeTouchListener(ctx: Context) : OnTouchListener {
    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(ctx, GestureListener())
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector!!.onTouchEvent(event)
    }

    private inner class GestureListener : SimpleOnGestureListener() {

        private val SWIPE_THRESHOLD = 50
        private val SWIPE_VELOCITY_THRESHOLD = 50

        override fun onDown(e: MotionEvent): Boolean {
            return super.onDown(e)
        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(distanceX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                    }
                    result = true
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(distanceY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom()
                    } else {
                        onSwipeTop()
                    }
                }
                result = true
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return result
        }
    }

    open fun onSwipeRight() {}
    open fun onSwipeLeft() {}
    open fun onSwipeTop() {}
    open fun onSwipeBottom() {}

}