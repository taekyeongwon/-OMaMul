package com.tkw.omamul.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cup(
    var cupId: String = "",
    var cupName: String = "",
    var cupAmount: Int = DEFAULT_CUP_AMOUNT,
    var createMode: Boolean = true
): Parcelable, Draggable {
    companion object {
        const val DEFAULT_CUP_AMOUNT = 1000
        const val DEFAULT_CUP_ID = "default_cup_id"
    }

    fun toMapRequest() = CupEntityRequest(
        cupId = this@Cup.cupId,
        cupName = this@Cup.cupName,
        cupAmount = this@Cup.cupAmount
    )
}

data class CupList(
    val cupId: String = Cup.DEFAULT_CUP_ID,
    val cupList: List<Cup> = listOf()
)

//Draggable 상속한 클래스는 리스트에서 drag 가능
interface Draggable