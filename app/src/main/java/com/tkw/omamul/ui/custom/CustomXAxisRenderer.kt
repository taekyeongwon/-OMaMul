package com.tkw.omamul.ui.custom

import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

class CustomXAxisRenderer(
    viewPortHandler: ViewPortHandler,
    xAxis: XAxis,
    trans: Transformer
): XAxisRenderer(viewPortHandler, xAxis, trans) {
    override fun computeAxisValues(min: Float, max: Float) {
        super.computeAxisValues(min, max)
        val axisMinimum = ceil(min).let {
            if(it == -0.0f) abs(it) else it
        }
        val axisMaximum = floor(max)
        val labelCount = mXAxis.labelCount

        val range = abs(axisMaximum - axisMinimum)
        val interval = floor(range / (labelCount - 1))
        mAxis.mEntryCount = labelCount

        if (mAxis.mEntries.size < labelCount) {
            // Ensure stops contains at least numStops elements.
            mAxis.mEntries = FloatArray(labelCount)
        }

        var v = axisMinimum

        for (i in 0 until labelCount) {
            mAxis.mEntries[i] = v
            v += interval
        }
    }
}