package com.tkw.omamul.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cup(
    var cupId: Int = -1,
    var cupName: String = "",
    var cupAmount: Int = DEFAULT_CUP_AMOUNT
): Parcelable {
    companion object {
        const val DEFAULT_CUP_AMOUNT = 1000
    }
}