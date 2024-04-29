package com.tkw.util

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import android.widget.TextView

fun TextView.animateByMaxValue(maxValue: Int) {
    ValueAnimator.ofInt(0, maxValue).apply {
        duration = 1000
        interpolator = LinearInterpolator()
        addUpdateListener {
            val value = it.animatedValue
            this@animateByMaxValue.text = String.format("%dmL", value)
        }
        start()
    }
}

fun TextView.animateByMaxValue(maxValue: Float) {
    ValueAnimator.ofFloat(0f, maxValue).apply {
        duration = 1000
        interpolator = LinearInterpolator()
        addUpdateListener {
            val value = it.animatedValue
            this@animateByMaxValue.text = String.format("%.1fL", value)
        }
        start()
    }
}