package com.example.relearnandroid.recyclerview.snapler

import android.annotation.SuppressLint
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

    private var mStartScrollY = 0f
    var mTotalScrollY = 0f

    val isSwipeUp: Boolean
        get() = mTotalScrollY < 0 && abs(mTotalScrollY) < 1073

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        // Disable fling
        return super.fling(velocityX, 0)
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mStartScrollY = e.rawY
            }
        }
        return super.onInterceptTouchEvent(e)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mStartScrollY = e.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                mTotalScrollY = e.rawY - mStartScrollY // 大于0 ，手指向下滑动 小于0 手指向上滑动
            }
        }
        return super.onTouchEvent(e)
    }

}

