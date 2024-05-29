package com.tkw.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.tkw.ui.databinding.CustomSwitchBinding

/**
 * 스위치뷰 track, thumb drawable 커스텀, CheckedChangeListener 설정하기 위한 뷰
 */
class CustomSwitchView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    ConstraintLayout(context, attrs, defStyle) {
    private val dataBinding: CustomSwitchBinding

    init {
        dataBinding = CustomSwitchBinding.inflate(LayoutInflater.from(context), this, true)
        initDrawable()
    }

    private fun initDrawable() {
        with(dataBinding) {
            customSwitch.trackDrawable = ResourcesCompat.getDrawable(
                resources,
                R.drawable.switch_track_selector,
                null
            )
            customSwitch.thumbDrawable = ResourcesCompat.getDrawable(
                resources,
                R.drawable.switch_thumb_selector,
                null
            )
        }
    }

    fun setCheckedChangeListener(block: (CompoundButton, Boolean) -> Unit) {
        dataBinding.customSwitch.setOnCheckedChangeListener(block)
    }

    fun setChecked(flag: Boolean) {
        dataBinding.customSwitch.isChecked = flag
    }
}