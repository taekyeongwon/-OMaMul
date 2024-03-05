package com.tkw.omamul.ui.custom

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView

class DividerDecoration(private val padding: Float)
    : RecyclerView.ItemDecoration() {
    private val paint = Paint()
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingStart + padding
        val right = parent.width - parent.paddingEnd - padding
        paint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)

        for(i in 0 until parent.childCount - 1) {   //맨 마지막 항목 안 그리도록 -1
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = (child.bottom + params.bottomMargin).toFloat()
            val bottom = top

            c.drawLine(left, top, right, bottom, paint)

        }
    }
}