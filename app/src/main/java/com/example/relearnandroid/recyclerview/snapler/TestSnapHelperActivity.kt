package com.example.relearnandroid.recyclerview.snapler

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
class TestSnapHelperActivity : BaseActivity() {

    private var mHalfScreenHeight = 0

    private val mItemDecoration by lazy {
        object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.set(0, 0, 0, mHalfScreenHeight - 250 * 3)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_snap_helper_activity)

        mHalfScreenHeight = getScreenH() / 2


        test_snap_helper_rv.apply {
            layoutManager = LinearLayoutManager(this@TestSnapHelperActivity)
            adapter = SnapHelperRvAdapter(arrayOfNulls(100))
            addItemDecoration(mItemDecoration)
        }
//        PagerSnapHelper().attachToRecyclerView(test_snap_helper_rv)
//        LinearSnapHelper().attachToRecyclerView(test_snap_helper_rv)
        MySnapHelper().attachToRecyclerView(test_snap_helper_rv)

    }

    companion object {
        fun startTestSnapHelperActivity(context: Context) {
            context.startActivity(Intent(context, TestSnapHelperActivity::class.java))
        }
    }

}