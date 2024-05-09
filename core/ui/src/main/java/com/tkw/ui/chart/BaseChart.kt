package com.tkw.ui.chart

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout

interface BaseChart<E> {
    /**
     * zoom, offset 등 기본 세팅.
     * label count는 x축 7, y축 5 고정
     */
    fun initDefault()

    /**
     * x축 세팅
     */
    fun initXAxis()

    /**
     * y축 세팅
     */
    fun initYAxis()

    /**
     * y축 점선 세팅
     */
    fun setLimit(limit: Float)

    /**
     * x축, y축 unit 세팅
     */
    fun setUnit(xUnit: String, yUnit: String)

    /**
     * y축만 unit 세팅
     * (x축에 MM/dd 등 단위 없이 표시하는 경우)
     */
    fun setYUnit(yUnit: String)

    /**
     * x축 value format 세팅 (ex: format = MM/dd, values = x축에 표시될 값 어레이)
     */
    fun setXValueFormat(values: Array<String>)

    fun setMarker(markerType: MarkerType)

    /**
     * 차트에서 사용하는 데이터 형태에 맞게 파싱해서 리턴
     * (ex: MPAndroidChart의 경우 BarEntry 등)
     */
    fun parsingChartData(x: Float, y: Float): E

    /**
     * 차트 표시
     */
    fun setChartData(list: List<E>)

    /**
     * x축 최소, 최대 범위 지정
     */
    fun setXMinMax(min: Float, max: Float)

    /**
     * limit보다 값이 커지는 경우 y축 5번째 값 계산 하기 위한 함수
     */
    fun calculateYMaximum()
}