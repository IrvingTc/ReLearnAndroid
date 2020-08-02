package com.example.relearnandroid.kotlin.coroutine

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 *
 * @ClassName: util
 * @Description: TODO
 * @Author: tucheng
 * @Date: 2020/8/2 21:58
 * @Version 1.0
 *
 **/
suspend fun Animator.awaitEnd() = suspendCancellableCoroutine<Unit> { cont ->
    // 当协程被取消的时候，调用animator.cancel()
    cont.invokeOnCancellation { cancel() }

    addListener(object : AnimatorListenerAdapter() {
        private var isEndSuccessfully = true

        override fun onAnimationCancel(animation: Animator?) {
            isEndSuccessfully = false
        }

        override fun onAnimationEnd(animation: Animator?) {
            // 为了在协程恢复后的不发生泄漏，需要确保移除监听
            removeListener(this)

            if (cont.isActive) {
                //协程处于活跃状态
                if (isEndSuccessfully) {
                    //动画正常结束
                    Log.d("Testetst", "onAnimationEnd")
                    cont.resume(Unit)
                } else {
                    cont.cancel()
                }
            }
        }

    })

}