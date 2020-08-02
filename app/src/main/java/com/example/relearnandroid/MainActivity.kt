package com.example.relearnandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.relearnandroid.jetpack.lifecycle.TestLifeCycleActivity
import com.example.relearnandroid.kotlin.coroutine.first.TestCoroutineFirstAc
import com.example.relearnandroid.ui.canvas.ClipPathViewActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clip_path_view_test.setOnClickListener {
            ClipPathViewActivity.startClipPathViewActivity(this)
        }

        test_lifecycle.setOnClickListener {
            TestLifeCycleActivity.startTestLifeCycleActivity(this)
        }

        test_coroutine_first.setOnClickListener {
            TestCoroutineFirstAc.startTestCoroutineFirstAc(this)
        }
    }

}