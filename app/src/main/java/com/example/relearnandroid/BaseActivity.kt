package com.example.relearnandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.BarUtils

/**
 *
 * @ClassName: BaseActivity
 * @Description: TODO
 * @Author: tucheng
 * @Date: 2020/8/5 22:15
 * @Version 1.0
 *
 **/
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarVisibility(this, false)
    }

}