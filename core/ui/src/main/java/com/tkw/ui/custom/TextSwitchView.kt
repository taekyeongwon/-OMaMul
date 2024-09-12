package com.tkw.ui.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.tkw.ui.R
import com.tkw.ui.databinding.CustomTextImageBinding

/**
 * 텍스트 뷰 우측에 스위치 버튼 표시할 커스텀 뷰
 */
class TextSwitchView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : ConstraintLayout(context, attrs, defStyle) {
    lateinit var dataBinding: CustomTextImageBinding

    init {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet? = null) {
        dataBinding = CustomTextImageBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )

        dataBinding.svSwitch.visibility = View.VISIBLE

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
                dataBinding.tvText.text = getString(R.styleable.CustomTextImage_text)
                dataBinding.tvText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                dataBinding.tvText.setTextColor(getColor(R.styleable.CustomTextImage_textColor, Color.BLACK))
            } finally {
                recycle()
            }
        }
    }

    fun setChecked(flag: Boolean) {
        dataBinding.svSwitch.setChecked(flag)
    }

    fun getChecked(): Boolean = dataBinding.svSwitch.getChecked()

    fun setSwitch(flag: Boolean, block: (CompoundButton, Boolean) -> Unit) {
        dataBinding.svSwitch.setChecked(flag)
        dataBinding.svSwitch.setCheckedChangeListener(block)
    }

    fun clear() {
        dataBinding.svSwitch.visibility = View.GONE
    }
}