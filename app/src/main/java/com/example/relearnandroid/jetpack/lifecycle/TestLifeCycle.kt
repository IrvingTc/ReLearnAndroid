package com.example.relearnandroid.jetpack.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.relearnandroid.logDebug

/**
 *
 * @ClassName: TestLifeCycle
 * @Description: TODO
 * @Author: tucheng
 * @Date: 2020/8/1 9:55
 * @Version 1.0
 *
 **/
object TestLifeCycle : DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        logDebug("onCreate")
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        logDebug("onStart")
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        logDebug("onResume")
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        logDebug("onPause")
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        logDebug("onStop")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        logDebug("onDestroy")
    }

}
