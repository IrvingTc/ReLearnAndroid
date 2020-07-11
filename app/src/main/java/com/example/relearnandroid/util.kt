package com.example.relearnandroid

import android.content.Context
import android.graphics.Point
import android.view.WindowManager

fun Context.getScreenH(): Int {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val point = Point()
    display.getSize(point)
    return point.y
}

fun Context.getScreenW(): Int {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val point = Point()
    display.getSize(point)
    return point.x
}
