package com.example.relearnandroid.coor

import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout

/**
 *
 * @ClassName: MyBehabir
 * @Description: TODO
 * @Author: tucheng
 * @Date: 2020/12/7 22:32
 * @Version 1.0
 *
 **/
class MyBehavior : CoordinatorLayout.Behavior<TextView>() {

    init {
        
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: TextView,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return true
    }

}