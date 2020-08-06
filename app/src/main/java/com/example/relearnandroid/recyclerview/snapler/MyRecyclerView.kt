package com.example.relearnandroid.recyclerview.snapler

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 *
 * @ClassName: MyRecyclerView
 * @Description: TODO
 * @Author: tucheng
 * @Date: 2020/8/6 22:55
 * @Version 1.0
 *
 **/
class MyRecyclerView(context: Context, attributeSet: AttributeSet) :
    RecyclerView(context, attributeSet) {

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        return super.fling(velocityX, 0)
    }

    fun scrollToCenter(mHalfScreenHeight: Int, dy: Int) {
        if (dy == 0) return
        val absDy = abs(dy)

        val finalDy = if (absDy > mHalfScreenHeight) {
            if (dy > 0) {
                mHalfScreenHeight - dy
            } else {
                -(mHalfScreenHeight + dy)
            }
        } else 0

        smoothScrollBy(0, finalDy)
    }

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {

        return super.onInterceptTouchEvent(e)
    }

}