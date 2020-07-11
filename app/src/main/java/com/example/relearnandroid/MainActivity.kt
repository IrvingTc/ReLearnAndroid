package com.example.relearnandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import com.example.relearnandroid.ui.canvas.ClipPathViewActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clip_path_view_test.setOnClickListener {
            ClipPathViewActivity.startClipPathViewActivity(this)
        }

    }

}