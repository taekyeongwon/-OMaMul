package com.tkw.ui.chart.base

import com.tkw.ui.chart.marker.MarkerType

interface BaseChart<E> {
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

    /**
     * MarkerType : Day, Week, Month enum값에 따라 마커 설정
     */
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
}