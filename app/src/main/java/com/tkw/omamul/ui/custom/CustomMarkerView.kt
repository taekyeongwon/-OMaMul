package com.tkw.omamul.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.tkw.omamul.R

//일, 주, 월 별 커스텀 마커 클래스 만들기.
class CustomMarkerView(context: Context?, layoutResource: Int, private val unit: String)
    : MarkerView(context, layoutResource) {

    private val tvContent: TextView = findViewById(R.id.test_marker_view)

    // draw override를 사용해 marker의 위치 조정 (bar의 상단 중앙)
    override fun draw(canvas: Canvas) {
        canvas.translate(-(width / 2).toFloat(), -height.toFloat() )
        super.draw(canvas)
    }

    // entry를 content의 텍스트에 지정
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        var prevY = 0f
        val y = e?.y ?: 0f
        val lineDataSet: LineDataSet? = chartView?.data?.dataSets?.get(0) as? LineDataSet
        lineDataSet?.let {
            val index = it.getEntryIndex(e)
            if(index > 0) {
                prevY = it.getEntryForIndex(index - 1).y
            }
        }
        //세팅된 유닛이 ml -> 그대로, L -> 1000곱해서 ml
        val milliLiterUnit = context.getString(R.string.unit_ml)
        val literUnit = context.getString(R.string.unit_liter)
        when(unit) {
            milliLiterUnit -> {
                tvContent.text = String.format("+%.0f", y - prevY).plus(milliLiterUnit)
            }
            literUnit -> {
                tvContent.text = String.format("+%.0f", (y - prevY) * 1000).plus(milliLiterUnit)
            }
        }

        super.refreshContent(e, highlight)
    }

}