package com.example.relearnandroid.recyclerview.snapler

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.example.relearnandroid.recyclerview.snapler.SnapState.*
import kotlin.math.abs

/**
 *
 * @ClassName: CityVoiceSnapHelper
 * @Description: TODO
 * @Author: tucheng
 * @Date: 2020/8/16 21:54
 * @Version 1.0
 *
 **/

private const val TAG = "TestSnapHelper"

class TestSnapHelper {

    private lateinit var mVerticalHelper: OrientationHelper
    private var mRecyclerView: RecyclerView? = null

    // Handles the snap on scroll case.
    private val mOnScrollListener by lazy {
        object : RecyclerView.OnScrollListener() {
            private var mDragged = false

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when {
                    newState == SCROLL_STATE_DRAGGING -> {
                        mDragged = true
                    }
                    newState == SCROLL_STATE_IDLE && mDragged -> {
                        // Only align after the user manually drags the list to prevent us
                        // from repeating the alignment after aligning
                        Log.d(TAG, "mRecyclerView?.scrollY ${mRecyclerView?.scrollY}")
                        mDragged = false
                        snapToTargetExistingView()
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            }

        }
    }

    /**
     * Attaches the TestSnapHelper to the provided RecyclerView, by calling
     * You can call this method with {@code null} to detach it from the current RecyclerView.
     *
     * @param recyclerView The RecyclerView instance to which you want to add this helper or
     *                     {@code null} if you want to remove SnapHelper from the current
     *                     RecyclerView.
     */
    fun attachToRecyclerView(recyclerView: RecyclerView?) {
        if (mRecyclerView == recyclerView) {
            return // nothing to do
        }
        if (mRecyclerView != null) {
            mRecyclerView?.removeOnScrollListener(mOnScrollListener)
        }
        mRecyclerView = recyclerView
        mRecyclerView?.let {
            it.addOnScrollListener(mOnScrollListener)
            snapToTargetExistingView()
        }
    }

    /**
     * Snaps to a target view which currently exists in the attached {@link RecyclerView}. This
     * method is used to snap the view when the {@link RecyclerView} is first attached; when
     * snapping was triggered by a scroll and when the fling is at its final stages.
     */
    private fun snapToTargetExistingView() {
        val rv = mRecyclerView ?: return
        val layoutManager = rv.layoutManager ?: return
        val snapView = findSnapView(layoutManager) ?: return
        val snapDistance = calculateDistanceToFinalSnap(layoutManager, snapView)
        snapDistance.takeIf {
            it != 0
        }?.also {
            rv.smoothScrollBy(0, it)
        }
    }

    private fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        snapView: View
    ): Int {
        return if (layoutManager.canScrollVertically()) {
            getVerticalHelper(layoutManager).run {
                getDecoratedStart(snapView) - startAfterPadding
            }
        } else {
            0
        }
    }

    private fun findSnapView(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper = getVerticalHelper(layoutManager)
    ): View? {
        return if (layoutManager is LinearLayoutManager) {
            //如果当前完全可见的item为最后一个item，则停止对齐，防止最后一个item显示不全
            if (layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1) {
                return null
            }

            val snapViewPosition = when (getSnapState()) {
                SWIPE_UP_LESS_THAN_ONE_ITEM, SWIPE_UP_MORE_THAN_ONE_ITEM, SWIPE_DOWN_MORE_THAN_ONE_ITEM -> {
                    layoutManager.findFirstVisibleItemPosition() + 1
                }
                SWIPE_DOWN_LESS_THAN_ONE_ITEM -> {
                    layoutManager.findFirstVisibleItemPosition()
                }
            }

            return if (snapViewPosition == RecyclerView.NO_POSITION) {
                return null
            } else layoutManager.findViewByPosition(snapViewPosition)
        } else {
            null
        }
    }

    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        if (::mVerticalHelper.isInitialized) {
            if (mVerticalHelper.layoutManager != layoutManager) {
                mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
            }
        } else {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        }
        return mVerticalHelper
    }

    private fun getSnapState(): SnapState {
        val rv = mRecyclerView as MyRecyclerView
        val totalScrollY = rv.mTotalScrollY
        val absTotalScrollY = abs(totalScrollY)
        return if (totalScrollY < 0) {
            //向上滑动
            if (absTotalScrollY > 1500f) {
                //滑动距离超过半屏
                SWIPE_UP_MORE_THAN_ONE_ITEM
            } else {
                SWIPE_UP_LESS_THAN_ONE_ITEM
            }
        } else {
            //向下滑动
            if (absTotalScrollY > 1500f) {
                //滑动距离超过半屏
                SWIPE_DOWN_MORE_THAN_ONE_ITEM
            } else {
                SWIPE_DOWN_LESS_THAN_ONE_ITEM
            }
        }
    }

}

enum class SnapState {
    SWIPE_UP_LESS_THAN_ONE_ITEM,
    SWIPE_UP_MORE_THAN_ONE_ITEM,
    SWIPE_DOWN_MORE_THAN_ONE_ITEM,
    SWIPE_DOWN_LESS_THAN_ONE_ITEM
}