package com.example.relearnandroid

import android.content.Context
import android.graphics.Point
import android.util.Log
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

inline fun <reified T : Any> T.logDebug(message: String) {
    Log.d(T::class.java.simpleName + "Test", message)
}

inline fun <reified T : Any> T.logError(message: String) {
    Log.e(T::class.java.simpleName + "Test", message)
}

inline fun <reified T : Any> T.logInfo(message: String) {
    Log.i(T::class.java.simpleName + "Test", message)
}

