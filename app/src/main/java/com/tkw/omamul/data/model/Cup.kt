package com.tkw.omamul.data.model

import android.os.Parcelable
import io.realm.kotlin.types.MutableRealmInt
import kotlinx.parcelize.Parcelize
import org.mongodb.kbson.ObjectId

@Parcelize
data class Cup(
    var cupId: String = "",
    var cupName: String = "",
    var cupAmount: Int = DEFAULT_CUP_AMOUNT,
    var createMode: Boolean = true
): Parcelable, Draggable {
    companion object {
        const val DEFAULT_CUP_AMOUNT = 1000
    }

    fun toMapEntity() = CupEntity().apply {
        cupId = ObjectId(this@Cup.cupId)
        cupName = this@Cup.cupName
        cupAmount = this@Cup.cupAmount
    }
}

data class CupList(
    val cupId: String,
    val cupList: List<Cup>
)

//Draggable 상속한 클래스는 리스트에서 drag 가능
interface Draggable