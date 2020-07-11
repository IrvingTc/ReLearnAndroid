package com.example.relearnandroid.customizeview.canvas

/**
 *
 * @ClassName: ClipPathView
 * @Description: canvas练习，使用clipPath实现从中间一个圆逐渐放大到全屏(相反即可)的动画
 * @Author: tucheng
 * @Date: 2020/7/11 20:30
 * @Version 1.0
 *
 **/

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.Keep
import androidx.core.animation.addListener
import kotlin.math.ceil
import kotlin.math.sqrt

private const val DEFAULT_CANVAS_RADIUS = 255f * 3 / 2 //圆默认的半径

class ClipPathView : View {

    private lateinit var mPath: Path
    private lateinit var mRectF: RectF
    private lateinit var mPaint: Paint
    private var mHalfScreenHeight = 0f
    private var mHalfScreenWidth = 0f
    private var mCanvasRadius =
        DEFAULT_CANVAS_RADIUS
    private var mEndCanvasRadius = 0f
    private var mViewStatus: ViewStatus = ViewStatus.SHIRKED

    private val mExpandAnimator by lazy {
        getCanvasAnimator(isExpand = true, onStart = {
            mViewStatus = ViewStatus.EXPANDING
        }, onEnd = {
            mViewStatus = ViewStatus.EXPANDED
        })
    }

    private val mShrinkAnimator by lazy {
        getCanvasAnimator(isExpand = false, onStart = {
            mViewStatus = ViewStatus.SHIRKING
        }, onEnd = {
            mViewStatus = ViewStatus.SHIRKED
        })
    }

    private fun getCanvasAnimator(isExpand: Boolean, onStart: (Animator) -> Unit, onEnd: (Animator) -> Unit): Animator {
        val canvasRadius = if (isExpand) {
            mEndCanvasRadius
        } else DEFAULT_CANVAS_RADIUS
        return ObjectAnimator.ofFloat(this, "CanvasRadius", canvasRadius).apply {
            interpolator = LinearInterpolator()
            duration = 300
            addListener(
                onStart = onStart,
                onEnd = onEnd
            )
        }
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        mPaint = Paint().apply {
            color = Color.BLUE
            isAntiAlias = true
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mHalfScreenHeight = h / 2f
        mHalfScreenWidth = w / 2f
        mRectF = RectF(0f, 0f, w.toFloat(), h.toFloat())
        mPath = Path().apply { addCircle(mHalfScreenWidth, mHalfScreenHeight, mCanvasRadius, Path.Direction.CW) }
        mEndCanvasRadius = ceil(sqrt(mHalfScreenHeight * mHalfScreenHeight + mHalfScreenWidth * mHalfScreenWidth))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.apply {
            clipPath(mPath)
            drawRect(mRectF, mPaint)
        }
    }

    fun expand() {
        if (mViewStatus == ViewStatus.SHIRKED) {
            mExpandAnimator.start()
        }
    }

    fun shrink() {
        if (mViewStatus == ViewStatus.EXPANDED) {
            mShrinkAnimator.start()
        }
    }

    @Keep
    fun setCanvasRadius(radius: Float) {
        mCanvasRadius = radius
        mPath.apply {
            reset()
            addCircle(mHalfScreenWidth, mHalfScreenHeight, mCanvasRadius, Path.Direction.CW)
        }
        invalidate()
    }

    @Keep
    fun getCanvasRadius() = mCanvasRadius

    enum class ViewStatus {
        EXPANDING, EXPANDED, SHIRKED, SHIRKING
    }

}