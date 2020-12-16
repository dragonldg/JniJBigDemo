package com.android.customeview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources.NotFoundException
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.ldg.jbigjni.R
import java.util.*

class SignaturePad(
    context: Context,
    attrs: AttributeSet?
) : View(context, attrs) {
    // View state
    private var mPoints: MutableList<TimedPoint>? = null
    private var mIsEmpty = false
    private var mLastTouchX = 0f
    private var mLastTouchY = 0f
    private var mLastVelocity = 0f
    private var mLastWidth = 0f
    private val mDirtyRect: RectF

    // Configurable parameters
    private var mMinWidth = 0f
    private var mMaxWidth = 0f
    private var mVelocityFilterWeight = 0f
    private var mOnSignedListener: OnSignedListener? = null
    private val mPaint = Paint()
    private val mTextPaint = Paint()
    private val mPath = Path()
    private var mSignatureBitmap: Bitmap? = null
    private var mSignatureBitmapCanvas: Canvas? = null
    private var content = ""
    private val mTextBound: Rect
    fun setText(content: String) {
        this.content = content
        mTextPaint.getTextBounds(content, 0, content.length, mTextBound)
        invalidate()
    }

    /**
     * Set the pen color from a given resource. If the resource is not found,
     * [Color.BLACK] is assumed.
     *
     * @param colorRes the color resource.
     */
    @SuppressLint("ResourceType")
    fun setPenColorRes(colorRes: Int) {
        try {
            setPenColor(resources.getColor(colorRes))
        } catch (ex: NotFoundException) {
            setPenColor(resources.getColor(Color.BLACK))
        }
    }

    /**
     * Set the pen color from a given color.
     *
     * @param color the color.
     */
    fun setPenColor(color: Int) {
        mPaint.color = color
    }

    /**
     * Set the minimum width of the stroke in pixel.
     *
     * @param minWidth the width in pixel.
     */
    fun setMinWidth(minWidth: Float) {
        mMinWidth = minWidth
    }

    /**
     * Set the maximum width of the stroke in pixel.
     *
     * @param maxWidth the width in pixel.
     */
    fun setMaxWidth(maxWidth: Float) {
        mMaxWidth = maxWidth
    }

    /**
     * Set the velocity filter weight.
     *
     * @param velocityFilterWeight the weight.
     */
    fun setVelocityFilterWeight(velocityFilterWeight: Float) {
        mVelocityFilterWeight = velocityFilterWeight
    }

    fun clear() {
        mPoints = ArrayList<TimedPoint>()
        mLastVelocity = 0f
        mLastWidth = (mMinWidth + mMaxWidth) / 2
        mPath.reset()
        if (mSignatureBitmap != null) {
            mSignatureBitmap = null
            ensureSignatureBitmap()
        }
        setIsEmpty(true)
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val eventX = event.x
        val eventY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
                mPoints!!.clear()
                mPath.moveTo(eventX, eventY)
                mLastTouchX = eventX
                mLastTouchY = eventY
                addPoint(TimedPoint(eventX, eventY))
                resetDirtyRect(eventX, eventY)
                addPoint(TimedPoint(eventX, eventY))
            }
            MotionEvent.ACTION_MOVE -> {
                resetDirtyRect(eventX, eventY)
                addPoint(TimedPoint(eventX, eventY))
            }
            MotionEvent.ACTION_UP -> {
                resetDirtyRect(eventX, eventY)
                addPoint(TimedPoint(eventX, eventY))
                parent.requestDisallowInterceptTouchEvent(true)
                setIsEmpty(false)
            }
            else -> return false
        }

        // invalidate();
        invalidate(
            (mDirtyRect.left - mMaxWidth).toInt(), (mDirtyRect.top - mMaxWidth).toInt(),
            (mDirtyRect.right + mMaxWidth).toInt(), (mDirtyRect.bottom + mMaxWidth).toInt()
        )
        return true
    }

    override fun onDraw(canvas: Canvas) { //TODO
        if (content != "") canvas.drawText(
            content, (width - mTextBound.width()) / 2
                .toFloat(), (height + mTextBound.height()) / 2.toFloat(), mTextPaint
        )
        if (mSignatureBitmap != null) canvas.drawBitmap(mSignatureBitmap!!, 0f, 0f, mPaint)
    }

    fun setOnSignedListener(listener: OnSignedListener?) {
        mOnSignedListener = listener
    }

    private fun setIsEmpty(newValue: Boolean) {
        if (mIsEmpty != newValue) {
            mIsEmpty = newValue
            if (mOnSignedListener != null) if (mIsEmpty) mOnSignedListener!!.onClear() else mOnSignedListener!!.onSigned()
        }
    }

    private fun rotated(bmSnap: Bitmap?): Bitmap? {
        val matrix = Matrix()
        //			matrix.postRotate(-90);
        matrix.postRotate(0f)
        // create new bitmap from orig tranformed by matrix
        val bmr = Bitmap.createBitmap(
            bmSnap!!, 0, 0, bmSnap.width, bmSnap.height,
            matrix, true
        )
        if (bmr != null) return bmr
        return bmSnap // when all else fails
    }

    // Generate the required transform.
    //        canvas.drawColor(Color.WHITE);
    // canvas.drawBitmap(originalBitmap, 0, 0, null);
    // new antialised Paint
    // text color - #3D3D3D
    // text size in pixels
    // text shadow
    // paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
    // draw text to the Canvas center
    // draw text to the bottom
    fun getSignatureBitmap(): Bitmap {
        val originalBitmap = rotated(getTransparentSignatureBitmap())
        val whiteBgBitmap = Bitmap.createBitmap(
            DEFAULT_SCALE_WIDTH, DEFAULT_SCALE_HEIGHT,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(whiteBgBitmap)
        //        canvas.drawColor(Color.WHITE);
        // canvas.drawBitmap(originalBitmap, 0, 0, null);
        canvas.drawBitmap(
            originalBitmap!!,
            Rect(0, 0, originalBitmap.width, originalBitmap.height), Rect(
                0,
                0, DEFAULT_SCALE_WIDTH, DEFAULT_SCALE_HEIGHT
            ), null
        )
        // new antialised Paint
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        // text color - #3D3D3D
        paint.color = Color.BLACK
        // text size in pixels
        paint.textSize = 15f
        paint.textScaleX = 1.8f
        paint.style = Paint.Style.FILL
        // text shadow
        // paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
        // draw text to the Canvas center
        val bounds = Rect()
        paint.getTextBounds(content, 0, content.length, bounds)
        // draw text to the bottom
        val x = (DEFAULT_SCALE_WIDTH - bounds.width()) / 2
        val y = (DEFAULT_SCALE_HEIGHT - bounds.height()) / 2 + bounds.height()
        canvas.drawText(content, x.toFloat(), y.toFloat(), paint)
        return whiteBgBitmap
    }

    fun getTransparentSignatureBitmap(): Bitmap? {
        ensureSignatureBitmap()
        return mSignatureBitmap
    }

    fun getTransparentSignatureBitmap(trimBlankSpace: Boolean): Bitmap? {
        if (!trimBlankSpace) return getTransparentSignatureBitmap()
        ensureSignatureBitmap()
        val imgHeight = mSignatureBitmap!!.height
        val imgWidth = mSignatureBitmap!!.width

        // Trim width
        var width = 0
        for (i in 0 until imgHeight) for (j in imgWidth - 1 downTo 0) if (mSignatureBitmap!!.getPixel(
                j,
                i
            ) != Color.TRANSPARENT && j > width
        ) {
            width = j
            break
        }

        // Trim height
        var height = 0
        for (i in 0 until imgWidth) for (j in imgHeight - 1 downTo 0) if (mSignatureBitmap!!.getPixel(
                i,
                j
            ) != Color.TRANSPARENT && j > height
        ) {
            height = j
            break
        }
        return Bitmap.createBitmap(mSignatureBitmap!!, 0, 0, width, height)
    }

    private fun addPoint(newPoint: TimedPoint) {
        mPoints!!.add(newPoint)
        if (mPoints!!.size > 2) {
            // To reduce the initial lag make it work with 3 mPoints
            // by copying the first point to the beginning.
            if (mPoints!!.size == 3) mPoints!!.add(0, mPoints!![0])
            var tmp: ControlTimedPoints = calculateCurveControlPoints(
                mPoints!![0], mPoints!![1],
                mPoints!![2]
            )
            val c2: TimedPoint = tmp.c2
            tmp = calculateCurveControlPoints(mPoints!![1], mPoints!![2], mPoints!![3])
            val c3: TimedPoint = tmp.c1
            val curve = Bezier(mPoints!![1], c2, c3, mPoints!![2])
            val startPoint: TimedPoint = curve.startPoint
            val endPoint: TimedPoint = curve.endPoint
            var velocity: Float = endPoint.velocityFrom(startPoint)
            velocity = if (java.lang.Float.isNaN(velocity)) 0.0f else velocity
            velocity =
                mVelocityFilterWeight * velocity + (1 - mVelocityFilterWeight) * mLastVelocity

            // The new width is a function of the velocity. Higher velocities
            // correspond to thinner strokes.
            val newWidth = strokeWidth(velocity)

            // The Bezier's width starts out as last curve's final width, and
            // gradually changes to the stroke width just calculated. The new
            // width calculation is based on the velocity between the Bezier's
            // start and end mPoints.
            addBezier(curve, mLastWidth, newWidth)
            mLastVelocity = velocity
            mLastWidth = newWidth

            // Remove the first element from the list,
            // so that we always have no more than 4 mPoints in mPoints array.
            mPoints!!.removeAt(0)
        }
    }

    private fun addBezier(
        curve: Bezier,
        startWidth: Float,
        endWidth: Float
    ) {
        ensureSignatureBitmap()
        val originalWidth = mPaint.strokeWidth
        val widthDelta = endWidth - startWidth
        val drawSteps = Math.floor(curve.length().toDouble()).toFloat()
        var i = 0
        while (i < drawSteps) {

            // Calculate the Bezier (x, y) coordinate for this step.
            val t = i / drawSteps
            val tt = t * t
            val ttt = tt * t
            val u = 1 - t
            val uu = u * u
            val uuu = uu * u
            var x: Float = uuu * curve.startPoint.x
            x += 3 * uu * t * curve.control1.x
            x += 3 * u * tt * curve.control2.x
            x += ttt * curve.endPoint.x
            var y: Float = uuu * curve.startPoint.y
            y += 3 * uu * t * curve.control1.y
            y += 3 * u * tt * curve.control2.y
            y += ttt * curve.endPoint.y

            // Set the incremental stroke width and draw.
            mPaint.strokeWidth = startWidth + ttt * widthDelta
            mSignatureBitmapCanvas!!.drawPoint(x, y, mPaint)
            expandDirtyRect(x, y)
            i++
        }
        mPaint.strokeWidth = originalWidth
    }

    private fun calculateCurveControlPoints(
        s1: TimedPoint, s2: TimedPoint,
        s3: TimedPoint
    ): ControlTimedPoints {
        val dx1: Float = s1.x - s2.x
        val dy1: Float = s1.y - s2.y
        val dx2: Float = s2.x - s3.x
        val dy2: Float = s2.y - s3.y
        val m1 = TimedPoint((s1.x + s2.x) / 2.0f, (s1.y + s2.y) / 2.0f)
        val m2 = TimedPoint((s2.x + s3.x) / 2.0f, (s2.y + s3.y) / 2.0f)
        val l1 = Math.sqrt(dx1 * dx1 + dy1 * dy1.toDouble()).toFloat()
        val l2 = Math.sqrt(dx2 * dx2 + dy2 * dy2.toDouble()).toFloat()
        val dxm: Float = m1.x - m2.x
        val dym: Float = m1.y - m2.y
        val k = l2 / (l1 + l2)
        val cm = TimedPoint(m2.x + dxm * k, m2.y + dym * k)
        val tx: Float = s2.x - cm.x
        val ty: Float = s2.y - cm.y
        return ControlTimedPoints(
            TimedPoint(m1.x + tx, m1.y + ty), TimedPoint(
                m2.x
                        + tx, m2.y + ty
            )
        )
    }

    private fun strokeWidth(velocity: Float): Float {
        return Math.max(mMaxWidth / (velocity + 1), mMinWidth)
    }

    /**
     * Called when replaying history to ensure the dirty region includes all
     * mPoints.
     *
     * @param historicalX the previous x coordinate.
     * @param historicalY the previous y coordinate.
     */
    private fun expandDirtyRect(historicalX: Float, historicalY: Float) {
        if (historicalX < mDirtyRect.left) mDirtyRect.left =
            historicalX else if (historicalX > mDirtyRect.right) mDirtyRect.right = historicalX
        if (historicalY < mDirtyRect.top) mDirtyRect.top =
            historicalY else if (historicalY > mDirtyRect.bottom) mDirtyRect.bottom = historicalY
    }

    /**
     * Resets the dirty region when the motion event occurs.
     *
     * @param eventX the event x coordinate.
     * @param eventY the event y coordinate.
     */
    private fun resetDirtyRect(eventX: Float, eventY: Float) {

        // The mLastTouchX and mLastTouchY were set when the ACTION_DOWN motion
        // event occurred.
        mDirtyRect.left = Math.min(mLastTouchX, eventX)
        mDirtyRect.right = Math.max(mLastTouchX, eventX)
        mDirtyRect.top = Math.min(mLastTouchY, eventY)
        mDirtyRect.bottom = Math.max(mLastTouchY, eventY)
    }

    private fun ensureSignatureBitmap() {
        if (mSignatureBitmap == null) {
            mSignatureBitmap = Bitmap
                .createBitmap(width, height, Bitmap.Config.ARGB_8888)
            mSignatureBitmapCanvas = Canvas(mSignatureBitmap!!)
        }
    }

    interface OnSignedListener {
        fun onSigned()
        fun onClear()
    }

    companion object {
        private const val DEFAULT_SCALE_WIDTH = 320
        private const val DEFAULT_SCALE_HEIGHT = 144
    }

    init {
        val a = context.theme.obtainStyledAttributes(
            attrs, R.styleable.SignaturePad,
            0, 0
        )
        val resources = context.resources

        // Configurable parameters
        try {
            mMinWidth = a.getFloat(R.styleable.SignaturePad_minLWidth, 3f)
            mMaxWidth = a.getFloat(R.styleable.SignaturePad_maxLWidth, 7f)
            mVelocityFilterWeight = a.getFloat(R.styleable.SignaturePad_velocityFilterWeight, 0.9f)
            mPaint.color = a.getColor(
                R.styleable.SignaturePad_penColor,
                Color.BLACK
            )
        } finally {
            a.recycle()
        }
        // setDrawingCacheEnabled(true); // to save images
        // Fixed parameters
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeWidth = 6f
        // Dirty rectangle to update only the changed portion of the view
        mDirtyRect = RectF()
        clear()
        mTextPaint.isAntiAlias = true
        mTextPaint.style = Paint.Style.FILL
        mTextPaint.textScaleX = 2f
        mTextPaint.color = Color.BLACK // 绘画字体的颜色
        mTextPaint.textSize = 30f
        mTextBound = Rect()
    }
}