package com.example.relearnandroid.jetpack.lifecycle

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 *
 * @ClassName: TestLifeCycleActivity
 * @Description: TODO
 * @Author: tucheng
 * @Date: 2020/8/1 9:53
 * @Version 1.0
 *
 **/
class TestLifeCycleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(TestLifeCycle)

    }

    companion object {
        fun startTestLifeCycleActivity(context: Context) {
            context.startActivity(Intent(context, TestLifeCycleActivity::class.java))
        }
    }

}