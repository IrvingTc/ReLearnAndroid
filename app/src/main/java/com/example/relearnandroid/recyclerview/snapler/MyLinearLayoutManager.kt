package com.example.relearnandroid.recyclerview.snapler

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

/**
 *
 * @ClassName: MyLinearLayoutManager
 * @Description: TODO
 * @Author: tucheng
 * @Date: 2020/8/6 23:16
 * @Version 1.0
 *
 **/
class MyLinearLayoutManager(context: Context) : LinearLayoutManager(context) {

    var canScrollVertical = true

    override fun canScrollVertically(): Boolean {
        return super.canScrollVertically() && canScrollVertical
    }

}