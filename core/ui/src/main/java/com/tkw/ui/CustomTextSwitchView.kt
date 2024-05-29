package com.tkw.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.tkw.ui.databinding.CustomTextImageBinding

class CustomTextSwitchView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : ConstraintLayout(context, attrs, defStyle) {
    val dataBinding = CustomTextImageBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    init {
        dataBinding.svSwitch.visibility = View.VISIBLE
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet? = null) {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomTextImage,
            0,
            0
        )
        with(typedArray) {
            try {
                dataBinding.tvText.text = getString(R.styleable.CustomTextImage_text)
            } finally {
                recycle()
            }
        }
    }

    fun setSwitch(flag: Boolean, block: (CompoundButton, Boolean) -> Unit) {
        dataBinding.svSwitch.setChecked(flag)
        dataBinding.svSwitch.setCheckedChangeListener(block)
    }

    fun clear() {
        dataBinding.svSwitch.visibility = View.GONE
    }
}