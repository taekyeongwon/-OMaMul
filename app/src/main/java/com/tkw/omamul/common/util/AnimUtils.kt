package com.tkw.omamul.common.util

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import android.widget.TextView

fun TextView.animateByMaxValue(maxValue: Int) {
    ValueAnimator.ofInt(0, maxValue).apply {
        duration = 1000
        interpolator = LinearInterpolator()
        addUpdateListener {
            val value = it.animatedValue
            this@animateByMaxValue.text = value.toString()
        }
        start()
    }
}