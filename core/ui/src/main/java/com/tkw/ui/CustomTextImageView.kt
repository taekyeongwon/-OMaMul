package com.tkw.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.tkw.ui.databinding.CustomTextImageBinding

class CustomTextImageView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : ConstraintLayout(context, attrs, defStyle) {
    val dataBinding = CustomTextImageBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    init {
        isFocusableInTouchMode = true
        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) setSelected()
            else clear()
        }
        setOnClickListener {
            requestFocus()
        }

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
                dataBinding.ivImage.setImageDrawable(
                    getDrawable(R.styleable.CustomTextImage_src)
                )
                dataBinding.tvText.text = getString(R.styleable.CustomTextImage_text)
            } finally {
                recycle()
            }
        }
    }

    fun setSelected() {
        dataBinding.ivImage.visibility = View.VISIBLE
    }

    fun clear() {
        dataBinding.ivImage.visibility = View.GONE
    }
}