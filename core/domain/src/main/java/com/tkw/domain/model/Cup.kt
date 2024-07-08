package com.tkw.domain.model

import java.io.Serializable

data class Cup(
    var cupId: String = "",
    var cupName: String = "",
    var cupAmount: Int = DEFAULT_CUP_AMOUNT,
): Serializable {
    companion object {
        const val DEFAULT_CUP_AMOUNT = 1000
        const val DEFAULT_CUP_ID = "default_cup_id"
    }

    var createMode: Boolean = true
    var isChecked: Boolean = false

//    fun toMapEntity() = CupEntity().apply {
//        cupId = this@Cup.cupId
//        cupName = this@Cup.cupName
//        cupAmount = this@Cup.cupAmount
//    }
}

data class CupList(
    val cupId: String = Cup.DEFAULT_CUP_ID,
    val cupList: List<Cup> = listOf()
): Serializable