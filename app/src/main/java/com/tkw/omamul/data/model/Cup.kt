package com.tkw.omamul.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cup(
    var cupId: Int = -1,
    var cupName: String = "",
    var cupAmount: Int = DEFAULT_CUP_AMOUNT
): Parcelable, Draggable {
    companion object {
        const val DEFAULT_CUP_AMOUNT = 1000
    }
}

//Draggable 상속한 클래스는 리스트에서 drag 가능
interface Draggable