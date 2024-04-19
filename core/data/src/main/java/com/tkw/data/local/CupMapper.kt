package com.tkw.data.local

import com.tkw.database.model.CupEntity
import com.tkw.database.model.CupListEntity
import com.tkw.domain.model.Cup
import com.tkw.domain.model.CupList

object CupMapper {
    fun cupToEntity(cup: Cup): CupEntity {
        return CupEntity().apply {
            this.cupId = cup.cupId
            this.cupName = cup.cupName
            this.cupAmount = cup.cupAmount
        }
    }

    fun cupToModel(entity: CupEntity): Cup {
        return Cup(
            entity.cupId,
            entity.cupName,
            entity.cupAmount
        )
    }

    fun cupListToModel(entity: CupListEntity): CupList {
        return CupList(
            cupId = entity.cupId,
            cupList = entity.cupList.map {
                cupToModel(it)
            }
        )
    }
}