package com.tkw.ui.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.tkw.ui.R
import com.tkw.ui.databinding.CustomTextImageBinding

/**
 * 텍스트 뷰 우측에 이미지 표시할 커스텀 뷰
 */
class TextImageView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : ConstraintLayout(context, attrs, defStyle) {
    private lateinit var dataBinding: CustomTextImageBinding

    init {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet? = null) {
        dataBinding = CustomTextImageBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )
        dataBinding.ivImage.visibility = View.VISIBLE

        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomTextImage,
            0,
            0
        )
        with(typedArray) {
            try {
                val textSize = getDimension(
                    R.styleable.CustomTextImage_textSize,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15f, context.resources.displayMetrics)
                )
                dataBinding.ivImage.setImageDrawable(getDrawable(R.styleable.CustomTextImage_src))
                dataBinding.tvText.text = getString(R.styleable.CustomTextImage_text)
                dataBinding.tvText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                dataBinding.tvText.setTextColor(getColor(R.styleable.CustomTextImage_textColor, Color.BLACK))
            } finally {
                recycle()
            }
        }
    }

    fun setText(text: String) {
        dataBinding.tvText.text = text
    }
}