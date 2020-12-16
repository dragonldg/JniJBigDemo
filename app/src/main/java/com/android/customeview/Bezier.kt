package com.android.customeview

/**
 * 实现圆滑的曲线效果
 */
class Bezier(
    var startPoint: TimedPoint, var control1: TimedPoint, var control2: TimedPoint,
    var endPoint: TimedPoint
) {
    fun length(): Float {
        val steps = 10
        var length = 0
        var i: Int
        var t: Float
        var cx: Double
        var cy: Double
        var px = 0.0
        var py = 0.0
        var xdiff: Double
        var ydiff: Double
        i = 0
        while (i <= steps) {
            t = i / steps.toFloat()
            cx = point(t, startPoint.x, control1.x, control2.x, endPoint.x)
            cy = point(t, startPoint.y, control1.y, control2.y, endPoint.y)
            if (i > 0) {
                xdiff = cx - px
                ydiff = cy - py
                length += Math.sqrt(xdiff * xdiff + ydiff * ydiff).toInt()
            }
            px = cx
            py = cy
            i++
        }
        return length.toFloat()
    }

    fun point(
        t: Float,
        start: Float,
        c1: Float,
        c2: Float,
        end: Float
    ): Double {
        return start * (1.0 - t) * (1.0 - t) * (1.0 - t) + 3.0 * c1 * (1.0 - t) * (1.0 - t) * t + 3.0 * c2 * (1.0 - t) * t * t + end * t * t * t
    }

}