package com.tkw.ui.chart.renderer

import android.graphics.Canvas
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.renderer.YAxisRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler

class CustomYAxisRenderer(
    viewPortHandler: ViewPortHandler,
    yAxis: YAxis,
    trans: Transformer
    ): YAxisRenderer(viewPortHandler, yAxis, trans) {

    private var text: String = ""
    private val margin: Int = 10

    fun setUnit(unit: String) {
        text = unit
    }

    override fun drawYLabels(
        c: Canvas?,
        fixedPosition: Float,
        positions: FloatArray?,
        offset: Float
    ) {
        super.drawYLabels(c, fixedPosition, positions, offset)
        c!!.drawText(text, fixedPosition, positions!![positions.size - 1] - offset * 2 - margin, mAxisLabelPaint)
    }
}