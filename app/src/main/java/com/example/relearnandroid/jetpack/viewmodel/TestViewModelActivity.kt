package com.example.relearnandroid.jetpack.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.relearnandroid.BaseActivity

/**
 *
 * @ClassName: TestViewModelActivity
 * @Description: TODO
 * @Author: tucheng
 * @Date: 2020/8/2 19:53
 * @Version 1.0
 *
 **/
class TestViewModelActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelStore
        ViewModelProvider(this).get(TestViewModel::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}