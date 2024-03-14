package com.tkw.omamul.ui.custom.chart

import android.content.Context
import android.graphics.Canvas
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.tkw.omamul.R

//일, 주, 월 별 커스텀 마커 클래스 만들기.
class DayMarkerView(context: Context?, layoutResource: Int)
    : MarkerView(context, layoutResource) {

    private val tvAmount: TextView = findViewById(R.id.tv_amount)

    // draw override를 사용해 marker의 위치 조정 (bar의 상단 중앙)
    override fun draw(canvas: Canvas) {
        canvas.translate(-(width / 2).toFloat(), -height.toFloat() - 16f)
        super.draw(canvas)
    }

    // entry를 content의 텍스트에 지정
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        var prevY = 0f
        val y = e?.y ?: 0f
        val lineDataSet: LineDataSet? = chartView?.data?.dataSets?.get(0) as? LineDataSet
        lineDataSet?.let {
            val index = it.getEntryIndex(e)
            if(index > 0) { //선택 된 엔트리의 이전 인덱스 y값 가져오기
                prevY = it.getEntryForIndex(index - 1).y
            }
        }
        val milliLiterUnit = context.getString(R.string.unit_ml_no_bracket)
        tvAmount.text = String.format("+%.0f", y - prevY).plus(milliLiterUnit)

        super.refreshContent(e, highlight)
    }

}