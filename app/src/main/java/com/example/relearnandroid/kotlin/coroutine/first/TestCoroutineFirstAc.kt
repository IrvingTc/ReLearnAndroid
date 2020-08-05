package com.example.relearnandroid.kotlin.coroutine.first

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.relearnandroid.BaseActivity
import com.example.relearnandroid.R
import com.example.relearnandroid.kotlin.coroutine.awaitEnd
import kotlinx.android.synthetic.main.test_coroutine_first.*
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 *
 * @ClassName: TestCoroutineFirstAc
 * @Description: TODO
 * @Author: tucheng
 * @Date: 2020/8/2 21:10
 * @Version 1.0
 *
 **/
class TestCoroutineFirstAc : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_coroutine_first)

        lifecycleScope.launch {
            delay(2000)

            val animator1 = async {
                ObjectAnimator.ofFloat(test_coroutine_first_text, "translationY", 1000f).apply {
                    duration = 500
                    start()
                    awaitEnd()
                }
            }

            val testHardWork = async {
                testHardIOWork()
            }

            animator1.await()
            testHardWork.await()

            ObjectAnimator.ofFloat(test_coroutine_first_text, "translationY", 0f).apply {
                duration = 500
                start()
            }
        }
    }

    private suspend fun testHardIOWork() = suspendCancellableCoroutine<Unit> { cont ->
        lifecycleScope.launch {
            delay(10000)
            Log.d("Testetst", "Hard Work Ok")
            cont.resume(Unit)
        }
    }

    companion object {
        fun startTestCoroutineFirstAc(context: Context) {
            context.startActivity(Intent(context, TestCoroutineFirstAc::class.java))
        }
    }

}

