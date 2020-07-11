package com.example.relearnandroid.ui.canvas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.relearnandroid.R
import kotlinx.android.synthetic.main.clip_path_view_activity.*

class ClipPathViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.clip_path_view_activity)

        expand.setOnClickListener {
            clip_path_view.expand()
        }

        shrink.setOnClickListener {
            clip_path_view.shrink()
        }

    }

    companion object {
        fun startClipPathViewActivity(context: Context) {
            context.startActivity(Intent(context, ClipPathViewActivity::class.java))
        }
    }

}