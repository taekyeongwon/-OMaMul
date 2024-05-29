package com.tkw.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.tkw.ui.util.DimenUtils

class CustomCheckBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var isChecked = false
    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    init {
        setOnClickListener {
            toggle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isChecked) {
            val cx = width / 2
            val cy = height / 2
            val radius = (minOf(width, height) / 2).toFloat() - DimenUtils.dpToPx(context, 4)
            canvas.drawCircle(cx.toFloat(), cy.toFloat(), radius, paint)
        }
    }

    private fun toggle() {
        isChecked = !isChecked
        invalidate()
    }

    fun getChecked() = isChecked
}