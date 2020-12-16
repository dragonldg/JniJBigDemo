package com.android.customeview

class TimedPoint(val x: Float, val y: Float) {
    val timestamp: Long
    fun velocityFrom(start: TimedPoint): Float {
        val velocity = distanceTo(start) / (timestamp - start.timestamp)
        return if (velocity != velocity) 0f else velocity
    }

    fun distanceTo(point: TimedPoint): Float {
        return Math.sqrt(
            Math.pow(
                point.x - x.toDouble(),
                2.0
            ) + Math.pow(point.y - y.toDouble(), 2.0)
        ).toFloat()
    }

    init {
        timestamp = System.currentTimeMillis()
    }
}