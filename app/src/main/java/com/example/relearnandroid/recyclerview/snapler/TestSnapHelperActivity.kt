package com.example.relearnandroid.recyclerview.snapler

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.example.relearnandroid.BaseActivity
import com.example.relearnandroid.R
import com.example.relearnandroid.getScreenH
import com.example.relearnandroid.recyclerview.snapler.adapter.SnapHelperRvAdapter
import kotlinx.android.synthetic.main.test_snap_helper_activity.*

/**
 *
 * @ClassName: TestSnapHelperActivity
 * @Description: TODO
 * @Author: tucheng
 * @Date: 2020/8/5 21:16
 * @Version 1.0
 *
 **/

private const val TAG = "TestSnapHelperActivity"

class TestSnapHelperActivity : BaseActivity() {

    private var mHalfScreenHeight = 0
    private lateinit var myLinearLayoutManager: MyLinearLayoutManager
    private val mItemDecoration by lazy {
        object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.bottom = mHalfScreenHeight - 250 * 3
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_snap_helper_activity)

        mHalfScreenHeight = getScreenH() / 2
        myLinearLayoutManager = MyLinearLayoutManager(this@TestSnapHelperActivity)

        test_snap_helper_rv.apply {
            layoutManager = myLinearLayoutManager
            adapter = SnapHelperRvAdapter(arrayOfNulls(100))
            addItemDecoration(mItemDecoration)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                private var distanceY = 0
                private var mDragged = false

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    when (newState) {
                        SCROLL_STATE_IDLE -> {
                            Log.d(TAG, "SCROLL_STATE_IDLE")
                            if (mDragged) {
                                mDragged = false
                                (recyclerView as MyRecyclerView).scrollToCenter(
                                    mHalfScreenHeight,
                                    distanceY
                                )
                            }
                            distanceY = 0
                        }
                        SCROLL_STATE_DRAGGING -> {
                            Log.d(TAG, "SCROLL_STATE_DRAGGING")
                            mDragged = true
                        }
                        SCROLL_STATE_SETTLING -> {
                            Log.d(TAG, "SCROLL_STATE_SETTLING")
                        }
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy == 0) return
                    distanceY += dy
                }
            })
        }

//        MySnapHelper().attachToRecyclerView(test_snap_helper_rv)
    }

    companion object {
        fun startTestSnapHelperActivity(context: Context) {
            context.startActivity(Intent(context, TestSnapHelperActivity::class.java))
        }
    }

}