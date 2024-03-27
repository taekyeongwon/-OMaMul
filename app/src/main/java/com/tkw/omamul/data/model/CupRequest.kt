package com.tkw.omamul.data.model

data class CupEntityRequest(
    val cupId: String,
    val cupName: String,
    val cupAmount: Int
) {
    fun toMapEntity() = CupEntity().apply {
        cupId = this@CupEntityRequest.cupId
        cupName = this@CupEntityRequest.cupName
        cupAmount = this@CupEntityRequest.cupAmount
    }
}