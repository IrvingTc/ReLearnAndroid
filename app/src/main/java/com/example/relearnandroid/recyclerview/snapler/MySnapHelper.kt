package com.example.relearnandroid.recyclerview.snapler

import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

private const val TAG = "MySnapHelper"

class MySnapHelper : SnapHelper() {
    private lateinit var mVerticalHelper: OrientationHelper
    private lateinit var mRecyclerView: RecyclerView

    @Throws(IllegalStateException::class)
    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        mRecyclerView = recyclerView!!
        super.attachToRecyclerView(recyclerView)
    }

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray? {
        val out = IntArray(2)
        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToStart(targetView, getVerticalHelper(layoutManager))
        } else {
            out[1] = 0
        }
        return out
    }

    private fun distanceToStart(targetView: View, helper: OrientationHelper): Int {
        // 返回
        return helper.getDecoratedStart(targetView) - helper.startAfterPadding
    }

    override fun createSnapScroller(layoutManager: RecyclerView.LayoutManager): LinearSmoothScroller? {
        return if (layoutManager !is ScrollVectorProvider) {
            null
        } else object : LinearSmoothScroller(mRecyclerView.context) {
            override fun onTargetFound(
                targetView: View,
                state: RecyclerView.State,
                action: Action
            ) {
                val snapDistances =
                    calculateDistanceToFinalSnap(mRecyclerView.layoutManager!!, targetView)
                val dx = snapDistances!![0]
                val dy = snapDistances[1]
                val time = calculateTimeForDeceleration(
                    abs(dx).coerceAtLeast(abs(dy))
                )
                if (time > 0) {
                    action.update(dx, dy, time, mDecelerateInterpolator)
                }
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
            }
        }
    }

    /**
     * for fling
     */
    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager,
        velocityX: Int,
        velocityY: Int
    ): Int {
        if (layoutManager !is ScrollVectorProvider) {
            return RecyclerView.NO_POSITION
        }
        val itemCount = layoutManager.itemCount
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION
        }
        val currentView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        val currentPosition = layoutManager.getPosition(currentView)
        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION
        }
        val vectorProvider = layoutManager as ScrollVectorProvider
        // deltaJumps sign comes from the velocity which may not match the order of children in
        // the LayoutManager. To overcome this, we ask for a vector from the LayoutManager to
        // get the direction.
        val vectorForEnd = vectorProvider.computeScrollVectorForPosition(itemCount - 1)
            ?: // cannot get a vector for the given position.
            return RecyclerView.NO_POSITION

        //在松手之后,列表最多只能滚多一屏的item数
        val deltaThreshold = 1
//            layoutManager.width / getVerticalHelper(layoutManager).getDecoratedMeasurement(
//                currentView
//            )
        var hDeltaJump: Int
        if (layoutManager.canScrollVertically()) {
            hDeltaJump = estimateNextPositionDiffForFling(
                layoutManager,
                getVerticalHelper(layoutManager), velocityX, velocityY
            )
            if (hDeltaJump > deltaThreshold) {
                hDeltaJump = deltaThreshold
            }
            if (hDeltaJump < -deltaThreshold) {
                hDeltaJump = -deltaThreshold
            }
            if (vectorForEnd.y < 0) {
                hDeltaJump = -hDeltaJump
            }
        } else {
            hDeltaJump = 0
        }
        if (hDeltaJump == 0) {
            return RecyclerView.NO_POSITION
        }
        var targetPos = currentPosition + hDeltaJump
        if (targetPos < 0) {
            targetPos = 0
        }
        if (targetPos >= itemCount) {
            targetPos = itemCount - 1
        }
        return targetPos
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        return findStartView(layoutManager, getVerticalHelper(layoutManager))
    }

    private fun findStartView(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper?
    ): View? {
        return if (layoutManager is LinearLayoutManager) {
            val firstChildPosition =
                layoutManager.findFirstVisibleItemPosition()
            if (firstChildPosition == RecyclerView.NO_POSITION) {
                return null
            }
            if (layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1) {
                return null
            }
            //第一个可见的child view
            val firstChildView =
                layoutManager.findViewByPosition(firstChildPosition)
            Log.d(
                TAG,
                "end: ${helper!!.getDecoratedEnd(firstChildView)} totalHeight: ${helper.getDecoratedMeasurement(
                    firstChildView
                )}"
            )
//            if (helper!!.getDecoratedEnd(firstChildView) >= helper.getDecoratedMeasurement(
//                    firstChildView
//                ) / 2 && helper.getDecoratedEnd(firstChildView) > 0
//            ) {
//                //当第一个可见的child view的bottom(包括itemDecoration的bottom以及marginEnd)大于等于view的
//                // 一半高度时(包括itemDecoration和margin)，并且bottom大于0
//                //这个bottom是当前view的底部距父布局顶部的距离
//                firstChildView
//            } else {
//                layoutManager.findViewByPosition(firstChildPosition + 1)
//            }
            return firstChildView
        } else {
            null
        }
    }

    /**
     * for fling
     */
    private fun estimateNextPositionDiffForFling(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper, velocityX: Int, velocityY: Int
    ): Int {
        val distances = calculateScrollDistance(velocityX, velocityY)
        val distancePerChild = computeDistancePerChild(layoutManager, helper)
        if (distancePerChild <= 0) {
            return 0
        }
        val distance = distances[0]
        return if (distance > 0) {
            floor(distance / distancePerChild.toDouble()).toInt()
        } else {
            ceil(distance / distancePerChild.toDouble()).toInt()
        }
    }

    /**
     * for fling
     */
    private fun computeDistancePerChild(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper
    ): Float {
        var minPosView: View? = null
        var maxPosView: View? = null
        var minPos = Int.MAX_VALUE
        var maxPos = Int.MIN_VALUE
        val childCount = layoutManager.childCount
        if (childCount == 0) {
            return INVALID_DISTANCE
        }
        for (i in 0 until childCount) {
            val child = layoutManager.getChildAt(i)
            val pos = layoutManager.getPosition(child!!)
            if (pos == RecyclerView.NO_POSITION) {
                continue
            }
            if (pos < minPos) {
                minPos = pos
                minPosView = child
            }
            if (pos > maxPos) {
                maxPos = pos
                maxPosView = child
            }
        }
        if (minPosView == null || maxPosView == null) {
            return INVALID_DISTANCE
        }
        val start = helper.getDecoratedStart(minPosView)
            .coerceAtMost(helper.getDecoratedStart(maxPosView))
        val end =
            helper.getDecoratedEnd(minPosView).coerceAtLeast(helper.getDecoratedEnd(maxPosView))
        val distance = end - start
        return if (distance == 0) {
            INVALID_DISTANCE
        } else 1f * distance / (maxPos - minPos + 1)
    }

    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        if (!::mVerticalHelper.isInitialized) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        }
        return mVerticalHelper
    }

    companion object {
        private const val INVALID_DISTANCE = 1f
        private const val MILLISECONDS_PER_INCH = 40f
    }
}