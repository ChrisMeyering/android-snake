package com.meyering.snake.objects

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.atan2

interface OnSwipeListener: GestureDetector.OnGestureListener {
    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val angle = getAngle(e1.x, e1.y, e2.x, e2.y)
        val direction = Direction.fromAngle(angle)
        return onSwipe(direction)
    }

    fun onSwipe(d: Direction): Boolean

    override fun onShowPress(e: MotionEvent?) {

    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {

        return false
    }

    override fun onDown(e: MotionEvent?): Boolean {


        return true
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {

        return false
    }

    override fun onLongPress(e: MotionEvent?) {

    }



    fun getAngle(x1: Float, y1: Float, x2: Float, y2: Float) : Float {
        val radians = atan2(y1 - y2, x2- x1).toDouble()
        return Math.toDegrees(radians).toFloat()
    }

}