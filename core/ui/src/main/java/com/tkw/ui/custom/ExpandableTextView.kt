package com.tkw.ui.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.tkw.ui.R
import com.tkw.ui.databinding.CustomTextImageBinding

/**
 * 텍스트 뷰 클릭 시 아래로 뷰 펼치기 위한 커스텀 뷰
 */
class ExpandableTextView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : ConstraintLayout(context, attrs, defStyle){
    lateinit var dataBinding: CustomTextImageBinding

    private var expandListener: () -> Unit = {}
    private var collapseListener: () -> Unit = {}

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
                dataBinding.ivImage.setImageDrawable(
                    getDrawable(R.styleable.CustomTextImage_src)
                )
                dataBinding.tvText.text = getString(R.styleable.CustomTextImage_text)
                dataBinding.tvText.setTextColor(getColor(R.styleable.CustomTextImage_textColor, Color.BLACK))
            } finally {
                recycle()
            }
        }
    }

    fun setFocusable() {
        isFocusableInTouchMode = true
        setOnFocusChangeListener { _, hasFocus ->
            dataBinding.ivImage.visibility =
                if (hasFocus) {
                    expandListener()
                    View.VISIBLE
                }
                else {
                    collapseListener()
                    View.GONE
                }
        }
        setOnClickListener {
            requestFocus()
        }
    }

    fun setFocusChangeListener(
        expandListener: () -> Unit,
        collapseListener: () -> Unit
    ) {
        this.expandListener = expandListener
        this.collapseListener = collapseListener
    }

    /**
     * 뷰를 접었다 폈다 하기 위해 setFocusChangeListener 호출 후 사용
     */
    fun setSelected() {
        if(isFocusableInTouchMode) {
            requestFocus()
        } else {
            dataBinding.ivImage.visibility = View.VISIBLE
        }
    }

    fun clear() {
        if(isFocusableInTouchMode) {
            clearFocus()
        } else {
            dataBinding.ivImage.visibility = View.GONE
        }
    }
}